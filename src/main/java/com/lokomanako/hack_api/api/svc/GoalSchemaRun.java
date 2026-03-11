package com.lokomanako.hack_api.api.svc;

import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class GoalSchemaRun implements ApplicationRunner {

    private static final Pattern SAFE_NAME = Pattern.compile("^[A-Za-z0-9_]+$");

    @Autowired
    private JdbcTemplate jdbc;

    @Override
    public void run(ApplicationArguments args) {
        List<String> cons = oldGoalCons();
        for (String c : cons) {
            if (c == null || !SAFE_NAME.matcher(c).matches()) {
                continue;
            }
            try {
                jdbc.execute("alter table goal_item drop constraint " + c);
            } catch (Exception e) {
            }
        }
    }

    private List<String> oldGoalCons() {
        String sql = """
                select distinct tc.constraint_name
                from information_schema.table_constraints tc
                join information_schema.key_column_usage kcu
                  on kcu.constraint_name = tc.constraint_name
                 and kcu.table_schema = tc.table_schema
                where lower(tc.table_name) = 'goal_item'
                  and upper(tc.constraint_type) = 'UNIQUE'
                  and lower(kcu.column_name) = 'usr_id'
                """;
        try {
            return jdbc.queryForList(sql, String.class);
        } catch (Exception e) {
            return List.of();
        }
    }
}
