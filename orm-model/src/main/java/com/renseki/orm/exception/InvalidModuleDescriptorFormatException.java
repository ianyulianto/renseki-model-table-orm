package com.renseki.orm.exception;

public class InvalidModuleDescriptorFormatException extends RuntimeException {

    public InvalidModuleDescriptorFormatException() {
        super("Cannot find tag <module> OR there are more than 1 <module>!");
    }
}
