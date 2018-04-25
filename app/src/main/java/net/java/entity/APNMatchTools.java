package net.java.entity;

public class APNMatchTools {
    // 中国移动cmwap
    public static String CMWAP = "cmwap";

    // 中国移动cmnet
    public static String CMNET = "cmnet";

    // 中国联通3gwap APN

    public static String GWAP_3 = "3gwap";

    // 中国联通3gnet APN
    public static String GNET_3 = "3gnet";

    // 中国联通uni wap APN
    public static String UNIWAP = "uniwap";

    // 中国联通uni net APN
    public static String UNINET = "uninet";

    // 中国电信 ct wap APN
    public static String CTWAP = "ctwap";

    // 中国电信ct net　APN
    public static String CTNET = "ctnet";

    public static String matchAPN(String currentName)
    {

        if ("".equals(currentName) || null == currentName)
        {

            return "";
        }

        // 参数转为小写
        currentName = currentName.toLowerCase();
        // 检查参数是否与各APN匹配，返回匹配值
        if (currentName.startsWith(CMNET))
            return CMNET;
        else if (currentName.startsWith(CMWAP))
            return CMWAP;
        else if (currentName.startsWith(GNET_3))
            return GNET_3;

        else if (currentName.startsWith(GWAP_3))
            return GWAP_3;
        else if (currentName.startsWith(UNINET))
            return UNINET;

        else if (currentName.startsWith(UNIWAP))
            return UNIWAP;
        else if (currentName.startsWith(CTWAP))
            return CTWAP;
        else if (currentName.startsWith(CTNET))
            return CTNET;
        else if (currentName.startsWith("default"))
            return "default";
        else
            return "";
    }
}
