package com.longriver.kejiapower.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileIOUtils {

    public static void writeBytes(File file, StringBuilder stringBuilder) {
        FileOutputStream os = null;
        byte[] bytes = new byte[1024];
        try {
            os = new FileOutputStream(file);
            bytes = stringBuilder.toString().getBytes();
            //也可以直接使用o.write("String".getBytes());
            //因为字符串就是一个对象，能直接调用方法
            os.write(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Deprecated
    public static byte[] readBytes(File file, StringBuilder stringBuilder) {
        FileInputStream is = null;
        byte[] bytes = new byte[1024];

        try {
            is = new FileInputStream(file);
            //方式一：单个字符读取
            //需要注意的是，此处我用英文文本测试效果良好
            //但中文就悲剧了，不过下面两个方法效果良好
//            int ch = 0;
//            while((ch=is.read()) != -1){
//                System.out.print((char)ch);
//            }

            //方式二：数组循环读取
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                System.out.println(new String(bytes, 0, len));
            }

            //方式三：标准大小的数组读取
            /*
            //定一个一个刚好大小的数组
            //available()方法返回文件的字节数
            //但是，如果文件过大，内存溢出，那就悲剧了
            //所以，亲们要慎用！！！上面那个方法就不错
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            //因为数组大小刚好，所以转换为字符串时无需在构造函数中设置起始点
            System.out.println(new String(bytes));
            */

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }
}
