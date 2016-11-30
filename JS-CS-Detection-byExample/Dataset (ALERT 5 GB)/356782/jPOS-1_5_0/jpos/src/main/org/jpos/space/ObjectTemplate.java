/*
 * Copyright (c) 2004 jPOS.org 
 *
 * See terms of license at http://jpos.org/license.html
 *
 */
package org.jpos.space;

import java.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.Serializable;

public class ObjectTemplate implements Template {
    Object key;
    Object value;

    public ObjectTemplate (Object key, Object value) {
        super ();
        this.key    = key;
        this.value  = value;
    }
    public boolean equals (Object obj) {
        return value.equals (obj);
    }
    public Object getKey () {
        return key;
    }
}

