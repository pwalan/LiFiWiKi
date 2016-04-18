package lab414.bupt.com.lifiwiki.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.Callable;

import lab414.bupt.com.lifiwiki.utils.HttpOp;
import lab414.bupt.com.lifiwiki.utils.StringSubClass;

/**
 * Created by school-miao on 2016-03-21.
 */
public class RecogPic implements Callable{

    private String imageUri;

    public RecogPic(String imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public Object call() throws Exception {
        return whatTheFuckImage(imageUri);
    }

    private String whatTheFuckImage(String imageUrl){
        HttpOp hc=new HttpOp();
        StringSubClass ss=new StringSubClass();
        String temp=null;
        try {
            temp=hc.getHttp("http://image.baidu.com/n/pc_search?queryImageUrl="+ URLEncoder.encode(imageUrl, "utf-8")+"&fm=result&pos=&uptype=drag");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        temp=ss.subStringOne(temp, "guess-info-text", "</div>");
        if (temp==null) {
            return null;
        }
        temp = ss.subStringTwo(temp, "target=\"_blank\">", "</a>");

        return temp;
//		String[] tempData=ss.subStringAll(temp, "guess-info-word-link", "/a>");
//		for (int i = 0; i < tempData.length; i++) {
//			tempData[i]=ss.subStringOne(tempData[i], ">", "<");
//		}
//		return tempData;
    }

}
