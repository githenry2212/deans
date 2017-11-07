package com.gzcb.ams.batch.remote.partitioner;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RemoteBatchPartitioner implements Partitioner {

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Integer count = jdbcTemplate.queryForObject("select count(*) from t_credit_bill", Integer.class);
        int partitionSize = (count % gridSize == 0) ? (count / gridSize) : (count / gridSize + 1);
        Map<String, ExecutionContext> splitMap = new HashMap<>(partitionSize);
        for (int idx = 1; idx <= partitionSize; idx++) {
            ExecutionContext context = new ExecutionContext();
            int minIdx = (idx - 1) * gridSize + 1;
            context.put("min", minIdx);
            context.put("max", minIdx + gridSize - 1);
            splitMap.put("partition" + idx, context);
        }
        return splitMap;
    }
}
