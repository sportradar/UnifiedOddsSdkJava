package com.sportradar.unifiedodds.sdk.shared;

import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.URN;

import java.util.concurrent.ThreadLocalRandom;

public class StaticRandom {

    public static ThreadLocalRandom Instance = ThreadLocalRandom.current();

    public static String S() { return S(0); }

    public static String S(int limit) {
        int value = limit > 1 ? Instance.nextInt(1, limit) : Instance.nextInt();
        return String.valueOf(value);
    }

    public static int I() { return I(0); }

    public static int I(int limit) { return limit > 1 ? Instance.nextInt(1, limit) : Instance.nextInt(); }

    public static long L() { return L(0); }

    public static long L(long limit) { return limit > 1 ? Instance.nextLong(1, limit) : Instance.nextLong(); }

    public static double D() { return D(0); }

    public static double D(int limit) { return limit > 1 ? Instance.nextInt(0, limit) + Instance.nextDouble() : Instance.nextDouble(); }

    public static URN Urn(String type, int limit)
    {
        if (SdkHelper.stringIsNullOrEmpty(type))
        {
            type = "match";
        }
        long id = L(limit);

        return new URN("sr", type, id);
    }

    public static URN Urn(int id, String type)
    {
        if (SdkHelper.stringIsNullOrEmpty(type))
        {
            type = "match";
        }

        return new URN("sr", type, id);
    }

    public static String S1000() { return S(1000);}

    public static String S10000() { return S(10000); }

    public static String S10000P() { return S(900000000) + 100000; }

    public static int I1000() { return I(1000); }

    public static String S100() { return S(100); }

    public static int I100() { return I(100); }

    public static URN U1000() { return Urn("", 1000); }

    public static Boolean B() { return I(100) > 49; }

    public static double D0() { return D(0); }

    public static double D100() { return D(100); }
}
