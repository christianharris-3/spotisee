package com.spotisee.app.config;

import org.jdbi.v3.core.statement.Binding;
import org.jdbi.v3.core.statement.SqlLogger;
import org.jdbi.v3.core.statement.StatementContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlLogger implements SqlLogger {
    private static final Logger log = LoggerFactory.getLogger(MySqlLogger.class);

    @Override
    public void logBeforeExecution(StatementContext context) {
        String sql = context.getRenderedSql();
        sql = sql.replace("\n", "");
//        log.info("SQL CALL: {}", sql);
//        log.info("Bindings: {}", context.getBinding());
    }

}
