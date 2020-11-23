package main.java.com.longriver.kejiapower.utils;

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
}
