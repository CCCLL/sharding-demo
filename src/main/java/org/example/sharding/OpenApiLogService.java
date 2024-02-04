package org.example.sharding;

import org.example.sharding.dao.OpenApiLog;
import org.example.sharding.dao.OpenApiLogMapper;
import org.example.sharding.dao.OpenapiLogQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @Author: CCCLL
 */
@Service
public class OpenApiLogService {
    private final OpenApiLogMapper openApiLogMapper;

    public OpenApiLogService(OpenApiLogMapper openApiLogMapper) {
        this.openApiLogMapper = openApiLogMapper;
    }

    @Transactional
    public void bulkInsert(List<OpenApiLog> logs) {
        openApiLogMapper.bulkInsert(logs);
        //冗余数据，解决周期临界点数据不连续问题
        openApiLogMapper.bulkInsertRedundancy(logs);
    }

    public List<OpenApiLog> getLastWeek() {
        OpenapiLogQuery openapiLogQuery = new OpenapiLogQuery();
        openapiLogQuery.setFrom(Instant.now().minus(7, ChronoUnit.DAYS));
        openapiLogQuery.setTo(Instant.now());
        return openApiLogMapper.query(openapiLogQuery);
    }
}
