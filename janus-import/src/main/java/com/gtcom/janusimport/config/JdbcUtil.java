package com.gtcom.janusimport.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 51771 on 2018/3/22.
 */
public class JdbcUtil {
    protected static Logger logger = LoggerFactory.getLogger(JdbcUtil.class);

    private  Connection instance;
    private  Connection instanceMysql;
    SqlConfiguration gpConfiguration = new SqlConfiguration();

    public  synchronized Connection getInstanceGp(){

        if(instance==null){
            try {
                Class.forName("com.pivotal.jdbc.GreenplumDriver");
                instance = DriverManager.getConnection(gpConfiguration.GP_URL, gpConfiguration.GP_USER, gpConfiguration.GP_PASSWD);
            } catch (Exception e) {
                logger.error("创建greenplum连接失败");
                e.printStackTrace();
            }
        }
        return instance;
    }
    //获取mysql连接实例
    public  synchronized Connection getInstanceMysql(){
        if(instanceMysql==null){
            try {
                Class.forName("com.mysql.jdbc.Driver");
                instanceMysql = DriverManager.getConnection(gpConfiguration.MYSQL_URL,
                        gpConfiguration.MYSQL_USER,gpConfiguration.MYSQL_PASSWD);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("创建Mysql连接失败 ;"+e.getMessage());
            }
        }
        return instanceMysql;
    }

    public  synchronized Connection getInstanceMysql(String url,String user,String passwd){
        if(instanceMysql==null){
            try {
                Class.forName("com.mysql.jdbc.Driver");
                instanceMysql = DriverManager.getConnection(url, user,passwd);
            } catch (Exception e) {
                logger.error("创建greenplum连接失败");
                e.printStackTrace();
            }
        }
        return instanceMysql;
    }
    public static Map<String, String> getInstanceDomain(String path) {
        Map<String, String> domainMap = new HashMap<>();
        try {
            Files.lines(Paths.get(path)).sequential()
                    .filter(line->!"".equals(line))
                    .filter(line->line.split("\t").length==2)
                    .forEach(line->{
                        String[] tmpArr= line.split("\t");
                        domainMap.put(tmpArr[1],tmpArr[0]);
                    });
        } catch (IOException e) {
            logger.error("读取domain数据失败");
            e.printStackTrace();
        }
        return domainMap;
    }

    public static Map<String, String> getInstanceLuntan(String path) {
        Map<String, String> domainMap = new HashMap<>();
        try {
            Files.lines(Paths.get(path))
                    .filter(line->!"".equals(line))
                    .filter(line->line.split("\t").length==2)
                    .forEach(line->{
                        String[] tmpArr= line.split("\t");
                        domainMap.put(tmpArr[0],tmpArr[1]);
                    });
        } catch (IOException e) {
            logger.error("读取domain数据失败");
            e.printStackTrace();
        }
        return domainMap;
    }

    public static Map<String, String> getInstanceScoial(String path) {
        Map<String, String> domainMap = new HashMap<>();
        try {
            Files.lines(Paths.get(path))
                    .filter(line->!"".equals(line))
                    .filter(line->line.split("\t").length==2)
                    .forEach(line->{
                        String[] tmpArr= line.split("\t");
                        domainMap.put(tmpArr[0],tmpArr[1]);
                    });
        } catch (IOException e) {
            logger.error("读取domain数据失败");
            e.printStackTrace();
        }
        return domainMap;
    }


  /*  public static void main(String[] args) {

//        Map<String, String> domainMap=JdbcUtil.getInstanceDomain("E:\\greenplum\\网信办\\domain.txt");
//        System.out.println(domainMap.get("80give.com"));
//
//        Map<String, String> domainMap=JdbcUtil.getInstanceLuntan("E:\\greenplum\\网信办\\luntan.txt");
//        System.out.println(domainMap.get("xabbs.com"));


        try {
            JdbcUtil jdbcUtil =new JdbcUtil();
            Connection  db= jdbcUtil.getInstanceMysql();
            Statement st = db.createStatement();
//           String sql = "select pubdate,crawlDate from sensit_table where sensitiveCls!=''and crawlDate!=0 limit 10;";
//            String sql = "select pubdate,crawlDate from sensit_table where sensitiveCls='政治' and crawlDate!=0 ;";
            String sql="select pubdate,crawlDate from sensit_table where crawlDate!=0 and substring(pubdate,1,7)='2018-01';";
//            String sql = args[0];
            ResultSet rs = st.executeQuery(sql);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            long sum=0;
            long count=0;
            while (rs.next()) {
                String f1_Str=rs.getString(1);
                int f2_Int=rs.getInt(2);
                LocalDateTime time=  LocalDateTime.parse(f1_Str, formatter);
                long f1_Int=time.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                sum+=(f2_Int-f1_Int/1000);
//                System.out.println(f1_Int/1000+"\t"+f2_Int+"\t"+(f2_Int-f1_Int/1000));
                count++;
            }
            System.out.println(sum+"\t"+count);
            rs.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
