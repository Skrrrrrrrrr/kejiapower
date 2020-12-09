package com.longriver.kejiapower.utils;

import com.longriver.kejiapower.exceptions.DataFrameRuntimeException;
import com.longriver.kejiapower.exceptions.MyRuntimeException;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Program:StringUtils
 * Description:
 * Creation Time: 2020/11/19 19:41
 * author wangqi
 * Email:wangq.cn@hotmail.com
 * Since kejiapower
 */
public class StringUtils {

    static org.slf4j.Logger logger = LoggerFactory.getLogger(StringUtils.class);

    public static boolean isNotBlank(String str) {
        return true ? null == str || str.length() <= 0 : false;
    }


    /*高效读取（文件）长字符串*/
    public static String openStringFileIO(String path, String fileName) {
        long time = System.currentTimeMillis();
        String result = null;
        File f = new File(path, fileName);
        try {
            FileInputStream fileInputStream = new FileInputStream(f);
            StringBuilder buffer = new StringBuilder();
            String line;
            BufferedReader in = new BufferedReader(new InputStreamReader(fileInputStream));
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            result = buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("openStringFileIO " + (System.currentTimeMillis() - time));
        return result;
    }

    public static String getFileAddSpace(String replace, Integer blanks) {

        String regex = new StringBuffer().append("(.{").append(blanks.toString()).append("})").toString();
        replace = replace.replaceAll(regex, "$1 ");
        return replace;
    }

    /**
     * 字符串转换成为16进制(无需Unicode编码)
     *
     * @param str
     * @return
     */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        String regex = "^[A-Fa-f0-9]+$";
        if (!str.matches(regex)) {
            throw new RuntimeException("Cannot convert string to hex string, invalid String");
//            return true;
        }
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     *
     * @param hexStr
     * @return
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /*
     * 字节数组转16进制字符串
     */
    public static String bytesToHexString(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer(bArr.length);
        String sTmp;
        for (int i = 0; i < bArr.length; i++) {
            sTmp = Integer.toHexString(0xFF & bArr[i]);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp);
        }
        return sb.toString();
    }

    /**
     * @param n
     * @Title: intTohex
     * @Description: int型转换成16进制
     * @return: String
     */
    public static String intTohex(int n) {
        StringBuffer s = new StringBuffer();
        String a;
        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        while (n != 0) {
            s = s.append(b[n % 16]);
            n = n / 16;
        }
        a = s.reverse().toString();
        if ("".equals(a)) {
            a = "00";
        }
        if (a.length() == 1) {
            a = "0" + a;
        }
        return a;
    }


    /**
     * 字符串转16进制字符串
     *
     * @param strPart
     * @return
     */
    @Deprecated
    public static String string2HexString(String strPart) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString.append(strHex);
        }
        return hexString.toString();
    }

    /**
     * 十六进制转字节数组
     *
     * @param src
     * @return
     */
    public static byte[] hexString2Bytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer
                    .valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }


    /**
     * Hex字符串转byte
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }


    /**
     * hex字符串转byte数组
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    /**
     * 将数字转成ip地址
     *
     * @param IpNum 数字
     * @return 转换后的ip地址
     */
    public static String hexStr2Ip(String HexStr) {
        String regex = "^[A-Fa-f0-9]+$";
        if (!HexStr.matches(regex)) {
            throw new RuntimeException("Cannot convert string to hex string, invalid String");
//            return true;
        }
        StringBuilder sb = new StringBuilder("");
        sb.append(Integer.parseInt(HexStr.substring(0, 2), 16));
        sb.append('.');
        sb.append(Integer.parseInt(HexStr.substring(2, 4), 16));
        sb.append('.');
        sb.append(Integer.parseInt(HexStr.substring(4, 6), 16));
        sb.append('.');
        sb.append(Integer.parseInt(HexStr.substring(6, 8), 16));
        return sb.toString();

    }

    /**
     * 将ip 地址转换成数字
     *
     * @param ipAddress 传入的ip地址
     * @return 转换成数字类型的ip地址
     */
    public static String ip2HexStr(String ipAddress) {
        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        if (!ipAddress.matches(regex)) {
            throw new RuntimeException("Cannot convert string to hex string, invalid String");
//            return true;
        }
        StringBuilder sb = new StringBuilder("");
        String[] ip = ipAddress.split("\\.");
        sb.append(String.format("%02X", Integer.parseInt(ip[0])));
        sb.append(String.format("%02X", Integer.parseInt(ip[1])));
        sb.append(String.format("%02X", Integer.parseInt(ip[2])));
        sb.append(String.format("%02X", Integer.parseInt(ip[3])));

        return sb.toString();
    }


    /**
     * 将数字转成ip地址
     *
     * @param IpNum 数字
     * @return 转换后的ip地址
     */
    public static String getNumConvertIp(long ipLong) {
        long mask[] = {0x000000FF, 0x0000FF00, 0x00FF0000, 0xFF000000};
        long num = 0;
        StringBuffer ipInfo = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            num = (ipLong & mask[i]) >> (i * 8);
            if (i > 0)
                ipInfo.insert(0, ".");
            ipInfo.insert(0, Long.toString(num, 10));
        }
        return ipInfo.toString();
    }

    /**
     * 将ip 地址转换成数字
     *
     * @param ipAddress 传入的ip地址
     * @return 转换成数字类型的ip地址
     */
    public static long getIpConvertNum(String ipAddress) {
        String[] ip = ipAddress.split("\\.");
        long a = Integer.parseInt(ip[0]);
        long b = Integer.parseInt(ip[1]);
        long c = Integer.parseInt(ip[2]);
        long d = Integer.parseInt(ip[3]);

        long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
        return ipNum;
    }
}
