package net.java.utils;

/**
 * Created by Administrator on 2018/3/12.
 */

public class ndkTest {
    static {
        System.loadLibrary("ndkTest");
    }
    public native String getTalk();
}
