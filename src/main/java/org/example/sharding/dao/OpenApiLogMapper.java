package org.example.sharding.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.sharding.Sharding;
import org.example.sharding.ShardingRedundancy;

import java.util.List;

/**
 * @Author: CCCLL
 */
@Mapper
@Sharding
public interface OpenApiLogMapper {

    int bulkInsert(List<OpenApiLog> logs);

    @ShardingRedundancy
    int bulkInsertRedundancy(List<OpenApiLog> logs);

    List<OpenApiLog> query(@Param("filter") OpenapiLogQuery filter);
}
