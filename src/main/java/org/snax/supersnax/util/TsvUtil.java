package org.snax.supersnax.util;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author maoth
 * @Date 2021-04-22
 * @Description tsv文件工具类
 */
public class TsvUtil {

    /**
     * tsv文件转list
     *
     * @param url   文件路径
     * @param title 列名
     * @return 默认返回一个Map list
     */
    public static Object readTsv(String url, String[] title) {
        List<Object> list = new ArrayList<>();
        try {
            // 创建tsv解析器settings配置对象
            TsvParserSettings settings = new TsvParserSettings();
            settings.getFormat().setLineSeparator("\n");
            TsvParser parser = new TsvParser(settings);
            DataInputStream in = new DataInputStream(new FileInputStream(url));
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            List<String[]> allRows = parser.parseAll(br);
            System.out.println(allRows.size());
            for (String[] allRow : allRows) {
                Map<String, String> map = new HashMap<>(16);
                for (int j = 0; j < title.length; j++) {
                    map.put(title[j], (allRow[j].trim()).replaceAll("\"", ""));
                }
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public static <T> InputStream listToInputStream(List<T> data) {

        // 保证线程安全
        StringBuilder buf = new StringBuilder();
        //获取类或接口的声明字段
        Field[] fields1 = data.get(0).getClass().getDeclaredFields();

        //存储表头信息
        for (Field field2 : fields1) {
            String fieldName1 = field2.getName();
            if ("serialVersionUID".equals(fieldName1)){
                continue;
            }
            buf.append(fieldName1).append(CSV_COLUMN_SEPARATOR);
        }
        buf.append(CSV_ROW_SEPARATOR);
        // 获取数据
        if (CollectionUtils.isNotEmpty(data)) {

            for (T t : data) {
                //获取类属性值
                Field[] fields = t.getClass().getDeclaredFields();

                for (Field field : fields) {
                    String fieldName = field.getName();
                    if ("serialVersionUID".equals(fieldName)){
                        continue;
                    }
                    //获取属性的get方法
                    String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    try {
                        Class<?> tCls = t.getClass();
                        Method getMethod = tCls.getMethod(getMethodName);
                        Object value = getMethod.invoke(t);
                        if (value == null) {
                            buf.append("").append(CSV_COLUMN_SEPARATOR);
                            continue;
                        }
                        //取值并赋给数组
                        String textValue = value.toString();
                        buf.append(textValue).append(CSV_COLUMN_SEPARATOR);
                    } catch (Exception e) {
                        LOGGER.error("list to input stream error , reason is {}", e.getMessage());
                    }
                }
                //迭代插入记录
                buf.append(CSV_ROW_SEPARATOR);
            }
        }
        return new ByteArrayInputStream(buf.toString().getBytes());
    }

    /**
     * 下载list
     *
     * @param dataList 数据list
     * @param fileName 文件名
     * @param response 响应
     * @param <T> 泛型对象
     * @throws IOException IO异常
     */
    public static <T> void downloadFile(List<T> dataList, String fileName, HttpServletResponse response)
        throws IOException {
        // 让servlet用GBK转码，默认为ISO8859
        response.setCharacterEncoding("GBK");
        if (dataList.isEmpty()) {
            // 让浏览器用UTF-8解析数据
            response.setHeader("Content-Type", "text/html;charset=GBK");
            response.getWriter().write("下载失败,请重新生成");
        } else {
            response.setContentType("text/tsv");
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));

            try (InputStream is = listToInputStream(dataList); OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    os.write(buffer, 0, len);
                }
            } catch (Exception e) {
                LOGGER.error("download file fail, reason is {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * TSV文件列分隔符
     */
    private static final String CSV_COLUMN_SEPARATOR = ",";

    /**
     * TSV文件行分隔符
     */
    private static final String CSV_ROW_SEPARATOR = "\r\n";

    private static final Logger LOGGER = LogManager.getLogger(TsvUtil.class);

    private TsvUtil() {

    }
}
