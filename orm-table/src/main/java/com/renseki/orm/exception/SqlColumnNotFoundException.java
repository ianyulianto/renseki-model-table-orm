package com.renseki.orm.exception;

public class SqlColumnNotFoundException extends RuntimeException {

    public SqlColumnNotFoundException(String table, String column) {
        super(String.format("[%s] %s", table, column));
    }
}
