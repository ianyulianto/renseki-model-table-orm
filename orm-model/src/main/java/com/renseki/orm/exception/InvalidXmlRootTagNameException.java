package com.renseki.orm.exception;

import com.renseki.orm.RensekiConstants;

public class InvalidXmlRootTagNameException extends RuntimeException {

    public InvalidXmlRootTagNameException(String tagName) {
        super(String.format("Found: '<%s>' Expected: '<%s>' OR '<%s>'",
            tagName,
            RensekiConstants.Xml.ROOT_TAG,
            RensekiConstants.Xml.LEGACY_ROOT_TAG));
    }
}
