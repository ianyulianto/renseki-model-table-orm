package com.renseki.orm.exception;

public class MismatchSqlTableJoinException extends RuntimeException {
    public MismatchSqlTableJoinException(String src, String target) {
        super(String.format("src: %s, target: %s", src, target));
    }
}
