package com.longriver.kejiapower.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Program:DataFrame
 * Description:
 * Creation Time: 2020/11/24 14:42
 * author wangqi
 * Email:wangq.cn@hotmail.com
 * Since kejiapower
 */
public class DataFrame {
    private StringBuffer sbf = new StringBuffer();

    public static boolean head(String dataFrame) {
        if ("FF".equals(String.valueOf(dataFrame).substring(0, 2))) {
            return true;
        }
        return false;
    }

    public static boolean isHeartBeat(String dataFrame) {

        if (null != dataFrame && dataFrame.length() > 11 && "0A".equals(String.valueOf(dataFrame).substring(4, 6))) {
            return true;
        }
        return false;
    }

    public static int dataFrameType(String dataFrame) {
        if (!dataFrameCheck(dataFrame)) return -1;

        if ("0A".equals(dataFrame.substring(4, 6))) return 0;//心跳帧
        else if ("0B".equals(dataFrame.substring(4, 6))) return 1;//控制帧
        else if ("0C".equals(dataFrame.substring(4, 6))) return 2;//上报帧
        else return -1;
    }

    public static boolean dataFrameCheck(String dataFrame) {
        if (!dataFrameHeadCheck(dataFrame) || !dataFrameTailCheck(dataFrame)) return false;
        return true;
    }

    private static boolean dataFrameHeadCheck(String dataFrame) {
        if (!notNullDataFrameCheck(dataFrame)) return false;
        if (!"FFFF".equals(dataFrame.substring(0, 4))) return false;
        return true;
    }

    private static boolean dataFrameTailCheck(String dataFrame) {
        if (!notNullDataFrameCheck(dataFrame)) return false;
        if (!"DD".equals(dataFrame.substring(dataFrame.length() - 2))) return false;
        return true;
    }

    private static boolean notNullDataFrameCheck(String dataFrame) {
        if (null == dataFrame) return false;
        return true;
    }

    public static int getID(String dataFrame)

    public static String respondHeartBeat(String dataFrame) {
        StringBuffer respondCount = new StringBuffer();
        switch (Integer.toHexString(Integer.valueOf(dataFrame.substring(6, 10)) + 1).length()) {
            case 1:
                respondCount.append('0').append('0').append('0').append(Integer.toHexString(Integer.valueOf(dataFrame.substring(6, 10)) + 1));
                break;
            case 2:
                respondCount.append('0').append('0').append(Integer.toHexString(Integer.valueOf(dataFrame.substring(6, 10)) + 1));
                break;
            case 3:
                respondCount.append('0').append(Integer.toHexString(Integer.valueOf(dataFrame.substring(6, 10)) + 1));
                break;
            case 4:
                respondCount.append(Integer.toHexString(Integer.valueOf(dataFrame.substring(6, 10)) + 1));
                break;
            default:
                break;
        }
        return new StringBuffer(dataFrame.substring(0, dataFrame.length() - 2)).replace(6, 10, String.valueOf(respondCount)).append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())).append("DD").toString();
    }


}
