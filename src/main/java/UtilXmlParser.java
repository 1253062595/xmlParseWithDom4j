package main.java;

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
        // 方案1
//        String rootDir = System.getProperty("user.dir");
//        System.out.println("rootDir路径---" + rootDir);
//        File xmlDir = new File(rootDir + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "mapper");
//        System.out.println("xml路径---" + xmlDir.getAbsolutePath());
//        long startTime = System.currentTimeMillis();
//        parseXMLFilse(xmlDir);
//        long endTime = System.currentTimeMillis();
//        System.out.println("方案1 耗时--" + (endTime - startTime) / 1000F);

        // 方案2
        long startTime2 = System.currentTimeMillis();
        test();
        long endTime2 = System.currentTimeMillis();
        System.out.println("方案2 耗时--" + (endTime2 - startTime2) / 1000F);
    }

    private static void test(){
        UtilXmlParser2 utilXmlParser2 = new UtilXmlParser2();
        List<String> mappersName = new ArrayList<>();
        mappersName.add("AuthMapper.xml");
        mappersName.add("CalendarMapper.xml");
        mappersName.add("ConfigureMapper.xml");
        mappersName.add("DataPermissionMapper.xml");
        mappersName.add("LoginMapper.xml");
        mappersName.add("LogMapper.xml");
        mappersName.add("MeMapper.xml");
        mappersName.add("OrgMapper.xml");
        mappersName.add("PermissionMapper.xml");
        mappersName.add("ResourceMapper.xml");
        mappersName.add("SysDataMapper.xml");
        mappersName.add("SysResourceMapper.xml");
        mappersName.add("TaskMapper.xml");
        mappersName.add("UserMapper.xml");
        utilXmlParser2.getPermTable(mappersName);
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
        for (String tableName : tableList) {
            System.out.println("方案1 has table ---" + tableName + "---");
        }
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
