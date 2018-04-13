package net.java.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StorageUtils {

    public static String getRAM(Context ctx) throws Exception{
        final String mem_path = "/proc/meminfo";// 系统内存信息文件，第一行为内存大小
        Reader reader = null;
        BufferedReader bufferedReader = null;
        reader = new FileReader(mem_path);
        bufferedReader = new BufferedReader(reader, 8192);
        long totalRAMSize = Long.parseLong(bufferedReader.readLine().split("\\s+")[1]) * 1024L;//这里*1024是转换为单位B（字节）
        return Formatter.formatFileSize(ctx,totalRAMSize);
    }

    //获取手机内部空间信息
   public static String getROM(Context ctx){
       final StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());//调用该类来获取磁盘信息（而getDataDirectory就是内部存储）
       long tcounts = statFs.getBlockCount();//总共的block数
       long counts = statFs.getAvailableBlocks() ; //获取可用的block数
       long size = statFs.getBlockSize(); //每格所占的大小，一般是4KB==
       long availROMSize = counts * size;//可用内部存储大小
       long totalROMSize = tcounts *size; //内部存储总大小
       return "存储总大小："+Formatter.formatFileSize(ctx,totalROMSize)+"  可用存储大小："+Formatter.formatFileSize(ctx,availROMSize);
   }

   //获取内置sd卡空间信息
   public static String getOut(Context ctx){
       final StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());//调用该类来获取磁盘信息（而getExternalStorageDirectory就是外置存储）
       long tcounts = statFs.getBlockCount();//总共的block数
       long counts = statFs.getAvailableBlocks() ; //获取可用的block数
       long size = statFs.getBlockSize(); //每格所占的大小，一般是4KB==
       long availROMSize = counts * size;//可用外部存储大小
       long totalROMSize = tcounts *size; //外部存储总大小
       return "存储总大小："+Formatter.formatFileSize(ctx,totalROMSize)+"  可用存储大小："+Formatter.formatFileSize(ctx,availROMSize);
   }

   public static String getSD(Context ctx){
       File sdcard_filedir = Environment.getExternalStorageDirectory();//得到sdcard的目录作为一个文件对象
       long usableSpace = sdcard_filedir.getUsableSpace();//获取文件目录对象剩余空间
       long totalSpace = sdcard_filedir.getTotalSpace();
       //将一个long类型的文件大小格式化成用户可以看懂的M，G字符串
       String usableSpace_str = Formatter.formatFileSize(ctx, usableSpace);
       String totalSpace_str = Formatter.formatFileSize(ctx, totalSpace);
       return "存储总大小："+totalSpace_str+"  可用存储大小："+usableSpace_str;
   }
    /**
     * 判断sd卡是否可用
     */
    public static boolean isExternalStorageAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }
}
