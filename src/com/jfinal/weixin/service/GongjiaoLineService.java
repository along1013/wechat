package com.jfinal.weixin.service;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.jfinal.weixin.entity.GongjiaoLine;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.jfinal.weixin.util.WeixinUtil;
public class GongjiaoLineService {
  /**
    * 爱帮公交线路查询
    * 根据城市和关键字（线路）查询
    * @param city  城市
    * @param q  线路
    * @return gongjiao
    */
  public static GongjiaoLine searchGongjiao(String city, String q) {
    // 爱帮api地址
    String requestUrl = "http://openapi.aibang.com/bus/lines?app_key=b7fb1e41aa104e9c24b81894d8e5ab48&city={city}&q={q}";
    // 对城市和线路进行编码
    requestUrl = requestUrl.replace("{city}", urlEncodeUTF8(city));
    requestUrl = requestUrl.replace("{q}", urlEncodeUTF8(q));
    // 处理名称、作者中间的空格
    requestUrl = requestUrl.replaceAll("\\+", "%20");
    // 查询并获取返回结果
    InputStream inputStream = HttpUtils.download(requestUrl, null);
    // 从返回结果中解析出gongjiao
    GongjiaoLine gj = parseGongjiao(inputStream);
  
    return gj;
  }
  /**
    * UTF-8编码
    *
    * @param source
    * @return
    */
  private static String urlEncodeUTF8(String source) {
    String result = source;
    try {
      result = java.net.URLEncoder.encode(source, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return result;
  }
  
  @SuppressWarnings("unchecked")
  private static GongjiaoLine parseGongjiao(InputStream inputStream) {
    GongjiaoLine gj = null;
    try {
      // 使用dom4j解析xml字符串
      SAXReader reader = new SAXReader();
      Document document = reader.read(inputStream);
      // 得到xml根元素
      Element root = document.getRootElement();
      // result_num表示查询得公交路线数量
      String num = root.element("result_num").getText();
      
      if (!"0".equals(num)) {
        // 公交信息根节点
        List<Element> lines = root.elements("lines");
        List<Element> line = lines.get(0).elements("line");
        
        String name = line.get(0).element("name").getText();
        String info = line.get(0).element("info").getText();
        String stats = line.get(0).element("stats").getText();
        String f_stats = line.get(1).element("stats").getText();
        gj = new GongjiaoLine();
        //获取公交始发站和终点站信息
        gj.setName(name);
        //获取公交价格
        gj.setJiage(info);
        //获取公交正向的各个站点
        gj.setZhengxiang(stats);
        //获取公交反向的信息
        gj.setFanxiang(f_stats);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return gj;
  }
  
  public static String Transit(String content){
	  String reslut="";
	  //将公交2个字及公交后面的+、空格、-等特殊符号去掉
      String keyWord = content.replaceAll("^公交[\\+ ~!@#%^-_=]?", "").trim();
      if(keyWord.equals("")){
      		reslut=getGuide();
      }else{
          String[] kwArr = keyWord.split("@");
          //城市
          String city = kwArr[0];
          String q = kwArr[1];
          if (q.indexOf("至")>0) {
			String[] startEnd=q.split("至"); 
			reslut=GongjiaoBusService.getgetGongjiaoBusSer(city, startEnd[0],startEnd[1]);
		}else {
			//搜索公交信息
	          GongjiaoLine gj = GongjiaoLineService.searchGongjiao(city, q);
	          //未搜索到公交信息
	          if(gj == null){
	        	  reslut="\ue252 对不起，没有找到你找的【"+city+"】城市或【"+q+"】公交线路。";        
	          }else{
	                   StringBuffer buffer = new StringBuffer();
	                     buffer.append(WeixinUtil.emoji(0x1F68C)).append(gj.getName()).append("\n\n");
	                     buffer.append(WeixinUtil.emoji(0x1F68C)).append(gj.getJiage()).append("\n\n");
	                     buffer.append(WeixinUtil.emoji(0x1F68C)).append(gj.getZhengxiang()).append("\n\n");
	                     buffer.append(WeixinUtil.emoji(0x1F68C)).append(gj.getFanxiang());
	                   
	                  reslut=buffer.toString();        
	          }
		}
          
     }
	return reslut;
	  
  }
  
  public static String getGuide(){
	  StringBuffer buffer = new StringBuffer();  
      buffer.append("\ue159 公交查询使用操作指南").append("\n\n");  
      buffer.append("\ue307 查询城市公交路线").append("\n");  
      buffer.append("格式：公交+城市的名称+@+公交路线").append("\n");  
      buffer.append("例如：公交深圳@m233").append("\n\n"); 
      
      buffer.append("\ue307 查询城市公交驾乘路线").append("\n"); 
      buffer.append("格式：公交+城市的名称+@+起点至终点").append("\n");  
      buffer.append("例如：公交深圳@油松派出所至深圳北站").append("\n\n");
      buffer.append("回复“?”显示主菜单");  
      return buffer.toString();  
  }
  // 测试方法
  public static void main(String[] args) {
    GongjiaoLine gj = searchGongjiao("营口", "206路");
    System.out.println("公交名称：" + gj.getName());
    System.out.println("公交价格：" + gj.getJiage());
    System.out.println("正向公交信息：" + gj.getZhengxiang());
    System.out.println("反向公交信息：" + gj.getFanxiang());
    String keyWord="ss@1234";
    String[] kwArr = keyWord.split("@");
    System.out.println(kwArr[0] +" "+kwArr[1]);
  }
}