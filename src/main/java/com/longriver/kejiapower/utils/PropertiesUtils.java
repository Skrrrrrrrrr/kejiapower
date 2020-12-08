package com.longriver.kejiapower.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Program:PropertiesUtils
 * Description:
 * Creation Time: 2020/11/22 16:12
 * author wangqi
 * Email:wangq.cn@hotmail.com
 * Since kejiapower
 */
public class PropertiesUtils {

    public void initProperties() {
        Properties properties = new Properties();
        String path = getClass().getClassLoader().getResource("lifeGame.properties").getPath();
        File file = new File(path);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            properties.load(inputStream);
//            delay = Integer.parseInt(properties.getProperty("delayTime"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
