package org.example.sharding;

import java.lang.annotation.*;



/**
 * @Author: CCCLL
 */
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ShardingRedundancy {
}
