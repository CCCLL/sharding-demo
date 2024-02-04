package org.example.sharding.dao;

import lombok.Data;

import java.time.Instant;

/**
 * @Author: CCCLL
 */
@Data
public class OpenApiLog {
    private Long id;

    private String api;

    private String param;

    private Instant occurredAt;

    private Instant createAt;

    private Instant lastModifiedAt;
}
