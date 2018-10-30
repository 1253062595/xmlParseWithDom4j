package main.java;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class UtilXmlParser2 {

    private String FLAG_LT = "/*lt*/";
    private String FLAG_LT_2 = "/*LT*/";
    private final List<String> key_list = new ArrayList<>(Arrays.asList("select", "delete", "insert", "insert", "update", "from", "where", "and", "on"));
    private List<String> tableList;
    private Logger logger = Logger.getLogger(UtilXmlParser.class.getSimpleName());

    public List<String> getPermTable(List<String> tableNameList) {
        tableList = new ArrayList<>();

        if (null == tableNameList || tableNameList.size() == 0) {
            logger.warning("getPermTable方法参数为空，请检查");
            return tableList;
        }

        for (int i = 0; i < tableNameList.size(); i++) {
            try {
                InputStream inputStream = this.getClass().getResourceAsStream("/main/resources/mapper/" + tableNameList.get(i));
                if (null != inputStream) {
                    logger.info("开始解析文件 --- " + tableNameList.get(i));
                    readFile(inputStream);
                } else {
                    logger.warning("解析文件 --- " + tableNameList.get(i) + " 失败，从其获取InputStream为空");
                }
            } catch (Exception e) {
                logger.warning("解析文件 --- " + tableNameList.get(i) + " 失败");
                e.printStackTrace();
            }
        }

        for (String tableName : tableList) {
            System.out.println("has table ---" + tableName + "---");
        }
        return tableList;
    }

    public void readFile(InputStream inputStream) throws IOException {
        InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line = "";
        while ((line = br.readLine()) != null) {
            if (line.contains(FLAG_LT) || line.contains(FLAG_LT_2)) {
                String temp = line.toLowerCase().replace("\n", "").replace("\t", " ").trim();
                parseString(temp);
            }
        }
        logger.info("此文件解析结束\n");
        br.close();
        isr.close();
        inputStream.close();
    }

    private void parseString(String string) {
        int index = string.indexOf(FLAG_LT);
        String part = string.substring(0, index).replace("\n", "").replace("\t", " ").trim();
        String[] array = part.trim().split(" ");
        String tableName = array[array.length - 1];
        if (!tableList.contains(tableName)) {
            if (key_list.contains(tableName)) {
                logger.warning("error: regard key word as table name. error string --- " + string);
            } else {
                tableList.add(tableName);
            }
        }
        string = string.substring(index + 6);
        if (string.contains(FLAG_LT)) {
            parseString(string);
        } else {
            return;
        }
    }
}
