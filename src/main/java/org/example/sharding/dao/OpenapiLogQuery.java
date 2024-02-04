package org.example.sharding.dao;

import lombok.Data;

import java.time.Instant;

/**
 * @Author: CCCLL
 */
@Data
public class OpenapiLogQuery {

    //需根据分表间隔（根据实际业务需求）限制查询区间
    private Instant from;

    private Instant to;

    private String api;

}
