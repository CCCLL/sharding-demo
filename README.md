使用Mybatis拦截器实现的简单、轻量的分表方案。并用冗余数据解决的临界点数据不连续问题，冗余数据的存储是串行的执行了两次SQL，有一定性能损耗。如果有条件可替换为监听增量binlog，可以使用cannal，监听增量binlog的方案性能更优。
