package com.gtcom.janusimport.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * Created by 51771 on 2017/10/25.
 */

public class FileUtil {
    protected static Logger logger = LoggerFactory.getLogger(FileUtil.class);
    public static String getRootPath() {
        return System.getProperty("user.dir");
    }

    public static String getPath(String folder) {
        return getRootPath() + File.separatorChar + folder;
    }
    //先从jar包所在当前目录读取，如果没有就从jar包年内读取
    public static Properties getPropertyFile(String file){
        String path=getRootPath()+File.separator+file;
        Properties prop=new Properties();
        InputStreamReader fis=null;
        try {
            fis=new InputStreamReader(new FileInputStream(path), "UTF-8");
            prop.load(fis);
            logger.info("读取配置文件:"+path);
        } catch (FileNotFoundException e) {
            return useClassLoader(file);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }


    public static boolean createFile(String dir, String filename, StringBuffer sb){
        File directory=new File(dir);
        if(!directory.exists()) directory.mkdirs();
        File file=new File(dir+File.separator+filename);
        BufferedWriter bw=null;
        try {
            bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            bw.write(sb.toString());
            bw.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    /**
     * 使用classloader加载属性文件
     * @param filename
     * @return
     */
    public static Properties useClassLoader(String filename) {
        logger.info("[从jar包中查找文件]:"+FileUtil.class.getClassLoader().getResource(filename));
        InputStream is = FileUtil.class.getClassLoader().getResourceAsStream(
                filename);
        Properties props = new Properties();
        try {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return props;
    }

    /**
     * 读json文件
     * @param filename json文件
     * @param charsetName
     * @return json对象
     */
    public static String readJson(String filename, String charsetName){
        String path=getRootPath()+File.separator+filename;
        if(filename.startsWith("/")){
            path = filename;
        }
        logger.info("[读取查询配置文件]"+path);
        File jsonFile=new File(path);
        if(!jsonFile.exists()) return null;
        BufferedReader br=null;
        StringBuffer sb=new StringBuffer();
        try {
            br=new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile), charsetName));
            String line;
            while((line=br.readLine())!=null){
                if(line.trim().startsWith("/") || line.trim().startsWith("#")) {
                    continue;
                }
                sb.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if(br!=null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static String readJson(String filename){
        return readJson(filename, "utf-8");
    }

/*    public static void main(String[] args) {
//        JSONArray jsonArray = JSONArray.fromObject(readJson("query.json"));
//        for(int i=0; i<jsonArray.size(); i++){
//            System.out.println(jsonArray.get(i));
//            JSONObject jsonObj=JSONObject.fromObject(jsonArray.get(i));
//            String queryConfig=jsonObj.getString("queryConfig");
//            System.out.println(queryConfig);
//            JSONObject jsonObj1=JSONObject.fromObject("queryConfig");
//            System.out.println(jsonObj1.getString("keyword"));
//
//        }
        logger.info("从jar包中查找文件:"+FileUtil.class.getClassLoader().getResource("es_config.properties"));

//        System.out.println(readJson("query.json").getString("keyWord"));
//        Properties prop=FileUtil.getPropertyFile("es_config.properties");
//		prop.list(System.out);
//        String path=getRootPath()+File.separator+"a.txt";
//        System.out.println(path);
//        System.out.println("---"+FileUtil.class.getClassLoader().getResource(""));
//        System.out.println("---"+FileUtil.class.getClassLoader().getResource("/"));
//        System.out.println("---"+FileUtil.class.getResource(""));
//        System.out.println("---"+FileUtil.class.getResource("/"));
    }*/


}
