package org.example.sharding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: CCCLL
 */
public class ShardingPropertyConfig {

    //此配置可存入数据库或配置中心
    public static final ConcurrentHashMap<String, ShardingProperty> SHARDING_TABLE = new ConcurrentHashMap<>();

    static {
        ShardingProperty apiLogShardingProperty = new ShardingProperty(3, LocalDate.parse("20240201", DateTimeFormatter.ofPattern("yyyyMMdd")), "openapi_log_test");
        ShardingProperty ggaLogShardingProperty = new ShardingProperty(7, LocalDate.parse("20240101", DateTimeFormatter.ofPattern("yyyyMMdd")), "gga_log");

        SHARDING_TABLE.put(apiLogShardingProperty.getTableName(), apiLogShardingProperty);
        SHARDING_TABLE.put(ggaLogShardingProperty.getTableName(), ggaLogShardingProperty);
    }
}
