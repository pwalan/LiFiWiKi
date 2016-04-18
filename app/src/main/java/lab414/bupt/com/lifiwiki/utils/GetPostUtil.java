package lab414.bupt.com.lifiwiki.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by school-miao on 2016-03-18.
 */
public class GetPostUtil {

    /**
     * 向指定URL发送GET方法的请求
     * @param url 发送请求的url
     * @param params 请求参数， 请求参数应该是 name1=value1&nname2=value2的形式
     * @return URL所代表远程资源的相应
     */

    public static String sendGet(String url, String params) {
        String result = "";
        BufferedReader in = null;

        try {
            String urlName = url + "?" + params;
            URL realUrl = new URL(urlName);
            //打开和url之间的连接
            URLConnection conn = realUrl.openConnection();
            //设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (Compatible; MSIE 6.0; Windows NT 5.1; SV1)");

            //建立实际的连接
            conn.connect();
            //获取所有相应头字段
            //获取所有响应字段
            Map<String, List<String>> map = conn.getHeaderFields();
            //遍历所有的响应头字段
            for(String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            //定义bufferedreader输入流来读取url的相应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while((line = in.readLine()) != null) {
                result += "\n" + line;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定URL发送POST方法的请求
     * @param url 发送请求的url
     * @param params 请求参数， 请求参数应该是 name1=value1&nname2=value2的形式
     * @return URL所代表远程资源的相应
     */
    public static String sendPost(String url, String params) {
        String result = "";
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            URL realUrl = new URL(url);
            //打开和url之间的连接
            URLConnection conn = realUrl.openConnection();
            //设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (Compatible; MSIE 6.0; Windows NT 5.1; SV1)");

            //发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);

            //获取urlconnection对象的输出流
            out = new PrintWriter(conn.getOutputStream());
            //发送请求参数
            out.print(params);
            //flush 输出流的缓冲
            out.flush();
            //定义bufferedreader输入流来读取url的相应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while((line = in.readLine()) != null) {
                result += "\n" + line;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(out != null) {
                    out.close();
                }
                if(in != null) {
                    in.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
