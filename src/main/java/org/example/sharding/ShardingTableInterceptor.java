package org.example.sharding;

import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: CCCLL
 */
@Slf4j
@Component
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class ShardingTableInterceptor implements Interceptor {
    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    private static final ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();
    private static final String MAPPED_STATEMENT = "delegate.mappedStatement";

    private static final String BOUND_SQL = "delegate.boundSql";
    private static final String ORIGIN_BOUND_SQL = "delegate.boundSql.sql";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private Pattern pattern = Pattern.compile("(from|into|update)[\\s]{1,}(\\w{1,})", Pattern.CASE_INSENSITIVE);


    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        // 根据mapper的注解判断是否进行分表操作
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue(MAPPED_STATEMENT);
        String id = mappedStatement.getId();
        String className = id.substring(0, id.lastIndexOf("."));
        Class<?> clazz = Class.forName(className);
        if (!clazz.isAnnotationPresent(Sharding.class)) {
            return invocation.proceed();
        }

        // 获取SQL
        BoundSql boundSql = (BoundSql) metaStatementHandler.getValue(BOUND_SQL);
        String originSql = (String) metaStatementHandler.getValue(ORIGIN_BOUND_SQL);
        if (StringUtils.isBlank(originSql)) {
            return invocation.proceed();
        }

        // 获取表名
        Matcher matcher = pattern.matcher(boundSql.getSql());
        String tableName;
        if (matcher.find()) {
            tableName = matcher.group(2).trim();
        } else {
            return invocation.proceed();
        }
        ShardingProperty shardingProperty = ShardingPropertyConfig.SHARDING_TABLE.get(tableName);
        if (shardingProperty == null) {
            return invocation.proceed();
        }

        // 获取新表名
        Method[] methods = clazz.getMethods();
        String methodName = id.substring(id.lastIndexOf(".") + 1);
        LocalDate tableNameDate = LocalDate.now();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                if (method.isAnnotationPresent(ShardingRedundancy.class)) {
                    tableNameDate = LocalDate.now().plusDays(shardingProperty.getDays());
                }
            }

        }
        String shardingTable = getCurrentShardingTable(shardingProperty, tableNameDate);
        if (shardingTable == null) {
            return invocation.proceed();
        }

        // 重构SQL，替换表名
        String rebuildSql = boundSql.getSql().replace(shardingProperty.getTableName(), shardingTable);
        metaStatementHandler.setValue(ORIGIN_BOUND_SQL, rebuildSql);
        log.info("originSql:[{}], rebuildSql:[{}]", originSql, rebuildSql);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
    }

    public static String getCurrentShardingTable(@NotNull ShardingProperty shardingProperty, @NotNull LocalDate now) {
        String tableName = shardingProperty.getTableName();
        Integer days = shardingProperty.getDays();
        LocalDate beginDate = shardingProperty.getBeginDate();


        if (now.isBefore(beginDate)) {
            return null;
        }

        LocalDate intervalStartDate = getIntervalStartDate(now, beginDate, days);
        LocalDate intervalEndDate = intervalStartDate.plusDays(days - 1);
        return tableName + "_" + intervalStartDate.format(FORMATTER) + "_" + intervalEndDate.format(FORMATTER);
    }

    public static LocalDate getIntervalStartDate(LocalDate targetDate, LocalDate startDate, int days) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, targetDate);
        long interval = daysBetween / days;
        return startDate.plusDays(interval * days);
    }
}