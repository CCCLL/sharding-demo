package org.example.sharding;

import org.example.sharding.dao.OpenApiLog;
import org.example.sharding.dao.OpenApiLogMapper;
import org.example.sharding.dao.OpenapiLogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: CCCLL
 */
@SpringBootApplication
@RestController
public class Application {

    @Autowired
    private OpenApiLogMapper openApiLogMapper;

    @Autowired
    private OpenApiLogService openApiLogService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("/openapilogs")
    public List<OpenApiLog> getCell(OpenapiLogQuery openapiLogQuery) {
        return openApiLogMapper.query(openapiLogQuery);
    }

    //为演示方便不做分页
    @GetMapping(value = "/openapilogs/lastweek")
    public List<OpenApiLog> getLastWeek() {
        return openApiLogService.getLastWeek();
    }

    @PostMapping("/openapilog/bulkSave")
    public void bulkSaveOpenApiLog(@RequestBody List<OpenApiLog> openApiLogs) {
        openApiLogService.bulkInsert(openApiLogs);
    }

}
