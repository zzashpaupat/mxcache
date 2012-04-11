package com.maxifier.mxcache.instrumentation;

/**
 * Created by IntelliJ IDEA.
 * User: dalex
 * Date: 19.10.2010
 * Time: 12:15:23
 */
public interface Instrumentator {
    ClassInstrumentationResult instrument(byte[] bytecode) throws IllegalCachedClass;
    
    String getVersion();
}