package cn.edu.sziit.hw.util;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

/**
 * 工具类，完成一些常用、公共的功能
 */
public class ToolUtils {
    /**
     * 全局唯一日志对象
     */
    private static Logger logger = Logger.getLogger("聊天室日志");
    /**
     * 存储头像列表
     */
    private static ArrayList profile_list;
    /**
     * 存储路径前缀
     */
    private static final String pathPrefix = "images/profiles/";
    /**
     * 存储头像列表大小
     */
    private static Integer num;
    /**
     * 添加头像文件名到列表
     */
    static {
        profile_list = new ArrayList<String>();
        profile_list.add(pathPrefix + "bear.png");
        profile_list.add(pathPrefix + "bee.png");
        profile_list.add(pathPrefix + "cat.png");
        profile_list.add(pathPrefix + "cattle.png");
        profile_list.add(pathPrefix + "deer.png");
        profile_list.add(pathPrefix + "dog.png");
        profile_list.add(pathPrefix + "giraffe.png");
        profile_list.add(pathPrefix + "horse.png");
        profile_list.add(pathPrefix + "lion.png");
        profile_list.add(pathPrefix + "monkey.png");
        profile_list.add(pathPrefix + "panda.png");
        profile_list.add(pathPrefix + "pig.png");
        profile_list.add(pathPrefix + "sheep.png");
        num = profile_list.size();
    }

    /**
     * 获得时间，年-月-日-时-分-秒
     * @return
     */
    public static String getRealTime() {
        return getRealTime("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获得时间，时/分/秒
     * @return
     */
    public static String getRealTimeByHour() {
        return getRealTimeByHour("HH:mm:ss");
    }

    /**
     * 获得时间，时分秒
     * @param pattern 提供的格式输出时间
     * @return
     */
    public static String getRealTimeByHour(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获得时间
     * @param pattern 按照指定的格式输出日期
     * @return
     */
    public static String getRealTime(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获得单例日志对象
     * @return
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * 给用户随机分配动物头像
     * @return
     */
    public static String getProfile() {
        return (String) profile_list.get(new Random().nextInt(num));
    }


    /**
     * 编码文件成二进制流
     * @param file 文件对象
     * @return 文件字节流
     * @throws FileNotFoundException
     */
    public static byte[] codeFile(File file) throws IOException {
        try (final FileInputStream fileInputStream = new FileInputStream(file))
        {
            return fileInputStream.readAllBytes();
        }
    }


    /**
     * 解码二进制成文件，并写入内容
     * @param content 文件内容
     * @param absolutePath 绝对路径
     * @return
     */
    public static void decodeFile(String content, String absolutePath) throws IOException {
        final File file = Path.of(absolutePath).toFile();
        if (! file.exists()) {
            file.createNewFile();
        }
        try (final FileOutputStream fileOutputStream = new FileOutputStream(file,true))
        {
            fileOutputStream.write(content.getBytes());
            fileOutputStream.flush();
        }
    }

    /**
     * 解析出文件后缀名
     * @param fileName 完整文件名，包括后缀名
     * @return 文件后缀名
     */
    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }


    


}
