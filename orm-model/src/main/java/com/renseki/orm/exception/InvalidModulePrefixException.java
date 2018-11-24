package com.renseki.orm.exception;

import com.renseki.orm.RensekiConstants;
import com.renseki.orm.descriptor.Descriptor;

public class InvalidModulePrefixException extends RuntimeException {
    public InvalidModulePrefixException(String path) {
        super("Prefix descriptor.xml seharusnya: " + RensekiConstants.Module.PREFIX + ". Found: " + path);
    }
}
