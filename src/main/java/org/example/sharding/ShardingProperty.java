package org.example.sharding;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @Author: CCCLL
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShardingProperty {
    // 分表周期天数
    private Integer days;
    // 分表开始日期，需要用这个日期计算周期表名
    private LocalDate beginDate;
    // 需要分表的表名
    private String tableName;
}
