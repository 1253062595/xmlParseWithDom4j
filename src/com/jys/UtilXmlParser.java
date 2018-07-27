package com.jys;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class UtilXmlParser {
    private static Logger logger = Logger.getLogger(UtilXmlParser.class.getSimpleName());
    private static String FLAG_LT = "/*lt*/";
    private static final List<String> key_list = new ArrayList<>(Arrays.asList("select", "delete", "insert", "insert", "update", "from", "where", "and", "on"));
    private static List<String> tableList = new ArrayList<>();

    public static void main(String[] args) {
        String rootDir = System.getProperty("user.dir");
        File xmlDir = new File(rootDir + File.separator + "xml");
        parseXMLFilse(xmlDir);
    }

    public static List<String> parseXMLFilse(File xmlDir) {
        if (null != tableList && tableList.size() > 0) {
            tableList.clear();
        }
        if (xmlDir.isDirectory()) {
            File[] xmlFiles = xmlDir.listFiles();
            if (xmlFiles.length > 0) {
                for (File file : xmlFiles) {
                    if ("xml".equals(file.getName().substring(file.getName().lastIndexOf(".") + 1))) {
                        if (null != file) {
                            if (file.exists()) {
                                parseFile(file);
                            }
                        }
                    }
                }
            } else {
                logger.warning("There is no file in this dir, please check.");
            }
        } else {
            logger.warning("Not a dir, please check.");
        }
        for (String tableName : tableList) { System.out.println("has table ---" + tableName + "---"); }
        return tableList;
    }

    private static void parseFile(File file) {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(file);
            Element rootElement = document.getRootElement();
            List<Element> selectList = rootElement.elements("select");
            for (Iterator<Element> it = selectList.iterator(); it.hasNext(); ) {
                Element element = it.next();
                String value = element.getStringValue().toLowerCase();
                if (null != value && value.contains(FLAG_LT)) {
                    parseString(value);
                }
            }
            List<Element> deleteList = rootElement.elements("delete");
            for (Iterator<Element> it = deleteList.iterator(); it.hasNext(); ) {
                Element element = it.next();
                String value = element.getStringValue().toLowerCase();
                if (null != value && value.contains(FLAG_LT)) {
                    parseString(value);
                }
            }
        } catch (DocumentException e) {
            logger.warning("read file " + file.getName() + " error");
            e.printStackTrace();
        }
    }

    private static void parseString(String string) {
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
