package com.amuselabs.test;

import org.apache.commons.logging.Log;

import java.io.PrintWriter;
import java.io.StringWriter;


public class TestUtils {

    public static String stackTrace(Throwable t) {
        StringWriter sw = new StringWriter(0);
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.close();
        return sw.getBuffer().toString();
    }

    public static void print_exception(String message, Throwable t, Log log) {
        String trace = stackTrace(t);
        String s = message + "\n" + t.toString() + "\n" + trace;
        if (log != null)
            log.warn(s);
        System.err.println(s);
    }
}
