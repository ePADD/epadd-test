package com.amuselabs.util;

import org.apache.commons.logging.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.Locale;

/**
 * Created by hangal on 3/17/17.
 */
public class Util {
    public static String localStorageKeyPrefix = "AL_PM";
    /** util method */
    private static String bytesToHexString(byte[] bytes)
    {
        // http://stackoverflow.com/questions/332079
        // http://stackoverflow.com/questions/7166129
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1)
                sb.append('0');
            sb.append(hex);
        }
        return sb.toString();
    }

    /** SHA-256 hash */
    public static String hash(String s)
    {
        MessageDigest digest = null;
        String hash = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(s.getBytes());
            hash = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return hash;
    }

    /** first n chars of of a hex string */
     public static String getPrefix(String str, int n) {
        if (str == null) {
            return null;
        }
        return str.substring(0, Math.min(str.length(), n));
    }

    public static boolean nullOrEmpty(String x) {
        return (x == null || "".equals(x));
    }

    public static <E> boolean nullOrEmpty(E[] a) {
        return (a == null || a.length == 0);
    }

    public static boolean nullOrEmpty(Collection c) {
        return (c == null || c.size() == 0);
    }

    public static boolean nullOrEmpty(Map m) {
        return (m == null || m.size() == 0);
    }

    public static boolean nullOrEmptyOrSpace(String x) {
        return (x == null || "".equals(x.trim()));
    }

    /**
     * escapes the 5 special html chars - see
     * http://www.w3schools.com/tags/ref_entities.asp
     */
    public static String escapeHTML(String str)
    {
        if (str == null)
            return null;

        // these are the 5 special xml chars according to
        // http://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
        str = str.replace("&", "&amp;");
        str = str.replace("'", "&apos;");
        str = str.replace("\"", "&quot;");
        str = str.replace("<", "&lt;");
        str = str.replace(">", "&gt;");
        return str;
    }

    public static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }

    public static void print_exception(String message, Throwable t, Log log)
    {
        String trace = stackTrace(t);
        String s = message + "\n" + t.toString() + "\n" + trace;
        if (log != null)
            log.warn(s);
        System.err.println(s);
    }

    public static void print_exception(Throwable t, Log log)
    {
        print_exception("", t, log);
    }

    public static void print_exception(Throwable t)
    {
        print_exception(t, null);
    }

    public static void report_exception(Throwable t)
    {
        print_exception(t);
        throw new RuntimeException(t);
    }

    public static String stackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter(0);
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.close();
        return sw.getBuffer().toString();
    }

    /** locale string is locale=en-US, etc. returns Locale.US by default. */
    public static Locale getLocale(String localeStr) {
        Locale locale = Locale.US; // default
        if (!Util.nullOrEmpty(localeStr)) {
            Locale[] locs = Locale.getAvailableLocales();
            for (Locale loc: locs)
                if (loc.toLanguageTag().equals(localeStr))
                    locale  = loc;
        }
        return locale;
    }
}
