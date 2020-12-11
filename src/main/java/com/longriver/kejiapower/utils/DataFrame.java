package com.longriver.kejiapower.utils;

import com.longriver.kejiapower.POJO.Message;

/**
 * Program:DataFrame
 * Description:
 * Creation Time: 2020/11/24 14:42
 * author wangqi
 * Email:wangq.cn@hotmail.com
 * Since kejiapower
 */
public class DataFrame {

    public static DataFrameType dataFrameTypeClassify(String dataFrame) throws RuntimeException {
        if (invalidDataFrameCheck(dataFrame)) throw new RuntimeException("Invalid DataFrame!");

        switch (dataFrame.substring(4, 6)) {
            case "0A":
                return DataFrameType.HeartBeat;//心跳帧
            case "0B":
                return DataFrameType.Control;//控制帧
            case "0C":
                return DataFrameType.Report;//上报帧
            default:
                throw new RuntimeException("Unrecognized Frame Type(Not HeartBeat or Control or Massage)");
        }
    }

    public static DataFrameType dataFrameTypeClassify(Message message) throws RuntimeException {
        if (invalidDataFrameCheck(message.toString())) throw new RuntimeException("Invalid DataFrame!");
        switch (message.getType().toString()) {
            case "0A":
                return DataFrameType.HeartBeat;//心跳帧
            case "0B":
                return DataFrameType.Control;//控制帧
            case "0C":
                return DataFrameType.Report;//上报帧
            default:
                throw new RuntimeException("Unrecognized Frame Type(Not HeartBeat or Control or Massage)");
        }
    }

    private static boolean dataFrameHeadCheck(String dataFrame) {
        if (!invalidDataFrameCheck(dataFrame)) return false;
        if (!"FFFF".equals(dataFrame.substring(0, 4))) return false;
        return true;
    }

    private static boolean dataFrameTailCheck(String dataFrame) {
        if (!invalidDataFrameCheck(dataFrame)) return false;
        if (!"DD".equals(dataFrame.substring(dataFrame.length() - 2))) return false;
        return true;
    }

    public static boolean invalidDataFrameCheck(String dataFrame) throws RuntimeException {
        if (!notBlankDataFrameCheck(dataFrame)) {
            throw new RuntimeException("Invalid DataFrame");
//            return true;
        }
        String regex = "^[A-Fa-f0-9]+$";
        if (!dataFrame.matches(regex) ||
                !"FFFF".equals(dataFrame.substring(0, 4)) ||
                !"DD".equals(dataFrame.substring(dataFrame.length() - 2))) {
            throw new RuntimeException("Cannot convert DataFrame to Hex,Invalid DataFrame");
//            return true;
        }
        if (dataFrame.length() <= 11)
            throw new RuntimeException("DataFrame may be too short, lacking information, pls check again carefully!");
        return false;
    }

    private static boolean notBlankDataFrameCheck(String dataFrame) {
        if (null == dataFrame || dataFrame.length() <= 0) return false;
        return true;
    }

    public static int getClinetData(String dataFrame) {
        return -2;
    }

    public static String getClientIP(String dataFrame) {
        if (!invalidDataFrameCheck(dataFrame)) {
            StringBuilder sb = new StringBuilder("");
            sb.append(Integer.parseInt(dataFrame.substring(12, 14), 16));
            sb.append('.');
            sb.append(Integer.parseInt(dataFrame.substring(14, 16), 16));
            sb.append('.');
            sb.append(Integer.parseInt(dataFrame.substring(16, 18), 16));
            sb.append('.');
            sb.append(Integer.parseInt(dataFrame.substring(18, 20), 16));
            return sb.toString();
        }
        throw new RuntimeException("DataFrame not contain a Client Ip Address!");
    }


}
