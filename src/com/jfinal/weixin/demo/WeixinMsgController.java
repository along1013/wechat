/**
 * Copyright (c) 2015-2016, Javen Zhou  (javenlife@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.jfinal.weixin.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jfinal.course.face.FaceService;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.SemanticApi;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;
import com.jfinal.weixin.sdk.jfinal.MsgControllerAdapter;
import com.jfinal.weixin.sdk.msg.in.InImageMsg;
import com.jfinal.weixin.sdk.msg.in.InLinkMsg;
import com.jfinal.weixin.sdk.msg.in.InLocationMsg;
import com.jfinal.weixin.sdk.msg.in.InShortVideoMsg;
import com.jfinal.weixin.sdk.msg.in.InTextMsg;
import com.jfinal.weixin.sdk.msg.in.InVideoMsg;
import com.jfinal.weixin.sdk.msg.in.InVoiceMsg;
import com.jfinal.weixin.sdk.msg.in.event.InCustomEvent;
import com.jfinal.weixin.sdk.msg.in.event.InFollowEvent;
import com.jfinal.weixin.sdk.msg.in.event.InLocationEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMassEvent;
import com.jfinal.weixin.sdk.msg.in.event.InMenuEvent;
import com.jfinal.weixin.sdk.msg.in.event.InQrCodeEvent;
import com.jfinal.weixin.sdk.msg.in.event.InShakearoundUserShakeEvent;
import com.jfinal.weixin.sdk.msg.in.event.InTemplateMsgEvent;
import com.jfinal.weixin.sdk.msg.in.event.InVerifyFailEvent;
import com.jfinal.weixin.sdk.msg.in.event.InVerifySuccessEvent;
import com.jfinal.weixin.sdk.msg.in.speech_recognition.InSpeechRecognitionResults;
import com.jfinal.weixin.sdk.msg.out.News;
import com.jfinal.weixin.sdk.msg.out.OutMusicMsg;
import com.jfinal.weixin.sdk.msg.out.OutNewsMsg;
import com.jfinal.weixin.sdk.msg.out.OutTextMsg;
import com.jfinal.weixin.sdk.msg.out.OutVoiceMsg;
import com.jfinal.weixin.sdk.utils.JsonUtils;
import com.jfinal.weixin.semantic.SearchObject;
import com.jfinal.weixin.service.BaiduAmbitus;
import com.jfinal.weixin.service.BaiduTranslate;
import com.jfinal.weixin.service.BaiduWeatherService;
import com.jfinal.weixin.service.GongjiaoLineService;
import com.jfinal.weixin.service.IdService;
import com.jfinal.weixin.service.PhoneService;
import com.jfinal.weixin.template.DataItem;
import com.jfinal.weixin.template.TempItem;
import com.jfinal.weixin.template.TempToJson;
import com.jfinal.weixin.util.WeixinUtil;

/**
 * 将此 DemoController 在YourJFinalConfig 中注册路由， 并设置好weixin开发者中心的 URL 与 token ，使
 * URL 指向该 DemoController 继承自父类 WeixinController 的 index
 * 方法即可直接运行看效果，在此基础之上修改相关的方法即可进行实际项目开发
 */
public class WeixinMsgController extends MsgControllerAdapter {
	public static String nearbyContent;// 附近
	public static String location;// 地理位置114.037125,22.645319
	public static String weahterContent;
	public String Regex = "[\\+ ~!@#%^-_=]?";
	static Log logger = Log.getLog(WeixinMsgController.class);
	private static final String helpStr = "您好！欢迎关注“白鹭资管”！点击下方菜单栏目即可了解企业相关信息。";

	/**
	 * 如果要支持多公众账号，只需要在此返回各个公众号对应的 ApiConfig 对象即可 可以通过在请求 url 中挂参数来动态从数据库中获取
	 * ApiConfig 属性值
	 */
	public ApiConfig getApiConfig() {
		ApiConfig ac = new ApiConfig();

		// 配置微信 API 相关常量
		ac.setToken(PropKit.get("token"));
		ac.setAppId(PropKit.get("appId"));
		ac.setAppSecret(PropKit.get("appSecret"));

		/**
		 * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
		 * 2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey", "setting it in config file"));
		return ac;
	}

	/**
	 * 实现父类抽方法，处理文本消息 本例子中根据消息中的不同文本内容分别做出了不同的响应，同时也是为了测试 jfinal weixin
	 * sdk的基本功能： 本方法仅测试了 OutTextMsg、OutNewsMsg、OutMusicMsg 三种类型的OutMsg，
	 * 其它类型的消息会在随后的方法中进行测试
	 */
	protected void processInTextMsg(InTextMsg inTextMsg) {
			OutTextMsg outMsg = new OutTextMsg(inTextMsg);
			outMsg.setContent(helpStr);
			render(outMsg);
//		String msgContent = inTextMsg.getContent().trim();
		// 帮助提示
//		if ("help".equalsIgnoreCase(msgContent) || "帮助".equals(msgContent)) {
//			OutTextMsg outMsg = new OutTextMsg(inTextMsg);
//			outMsg.setContent(helpStr);
//			render(outMsg);
//		}
//		else if (msgContent.equals("1") || msgContent.equals("人脸识别")) {
//			msgContent = "请发一张清晰的照片！" + WeixinUtil.emoji(0x1F4F7);
//			renderOutTextMsg(msgContent);
//		} else if (msgContent.equals("2") || msgContent.equals("在线翻译")) {
//			msgContent = BaiduTranslate.getGuide();
//			renderOutTextMsg(msgContent);
//		} else if (msgContent.startsWith("翻译")) {
//			try {
//				msgContent = BaiduTranslate.Translates(msgContent);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				msgContent = "\ue252 翻译出错了 \n\n" + BaiduTranslate.getGuide();
//			}
//			renderOutTextMsg(msgContent);
//		} else if (msgContent.equals("3") || msgContent.equals("天气查询")) {
//			msgContent = BaiduWeatherService.getGuide();
//			renderOutTextMsg(msgContent);
//		} else if (msgContent.startsWith("天气")) {
//			msgContent = msgContent.replaceAll("^天气" + Regex, "").trim();
//			if (!msgContent.equals("")) {
//				msgContent = BaiduWeatherService.getWeatherService(msgContent);
//				location = null;
//				weahterContent = null;
//			} else {
//				// 内容为空》》》》 天气
//
//				if (!StrKit.isBlank(location)) {
//					// 地址不为空
//					msgContent = BaiduWeatherService.getWeatherService(location);
//					location = null;
//					weahterContent = null;
//				} else {
//					msgContent = BaiduWeatherService.getGuide();
//				}
//
//			}
//			renderOutTextMsg(msgContent);
//		} else if (msgContent.equals("4") || msgContent.equals("公交查询")) {
//			msgContent = GongjiaoLineService.getGuide();
//			renderOutTextMsg(msgContent);
//
//		}else if (msgContent.equals("5") || msgContent.equals("手机归属地查询")) {
//			msgContent = PhoneService.getGuide();
//			renderOutTextMsg(msgContent);
//
//		} else if (msgContent.startsWith("归属地") || msgContent.contains("归属地@")) {
//			msgContent=msgContent.replaceAll("^归属地"+Regex,"");
//			msgContent = PhoneService.getPhoneInfo(msgContent);
//			renderOutTextMsg(msgContent);
//
//		} else if (msgContent.equals("6") || msgContent.equals("身份证查询")) {
//			msgContent = IdService.getGuide();
//			renderOutTextMsg(msgContent);
//
//		} else if (msgContent.startsWith("身份证") || msgContent.contains("身份证@")) {
//			msgContent=msgContent.replaceAll("^身份证"+Regex,"");
//			msgContent = IdService.getIdInfo(msgContent);
//			renderOutTextMsg(msgContent);
//
//		}  else if (msgContent.startsWith("公交")) {
//			msgContent = GongjiaoLineService.Transit(msgContent);
//			renderOutTextMsg(msgContent);
//		}else if (msgContent.equals("7") ||msgContent.startsWith("附近")) {
//			msgContent=msgContent.replaceAll("^附近"+Regex,"");
//			nearbyContent=msgContent;
//			if (location==null ||nearbyContent==null || location.trim().equals("") || nearbyContent.trim().equals("")) {
//				msgContent=BaiduAmbitus.getGuide();
//				renderOutTextMsg(msgContent);
//
//			}else {
//				List<News> ambitusService = BaiduAmbitus.getAmbitusService(nearbyContent, location);
//				if (ambitusService.size()>0) {
//					OutNewsMsg outMsg = new OutNewsMsg(inTextMsg);
//					outMsg.addNews(ambitusService);
//					render(outMsg);
//					nearbyContent=null;
//					location=null;
//					return ;
//				}else {
//					msgContent="\ue252 查询周边失败，请检查。";
//					renderOutTextMsg(msgContent);
//				}
//			}
//		}else if (msgContent.equals("8") || "开发者模式".equalsIgnoreCase(msgContent)) {
//			String url="开源中国中搜索Jfinal-weixin 或者Jfianl-qyweixin";
//			renderOutTextMsg(url);
//		}else if (msgContent.equals("9") || "QQ咨询".equalsIgnoreCase(msgContent)) {
//			String url="http://wpa.qq.com/msgrd?v=3&uin=572839485&site=qq&menu=yes";
//			String urlStr="<a href=\""+url+"\">点击咨询</a>";
//			renderOutTextMsg("QQ在线咨询"+urlStr);
//		}else if (msgContent.equals("10") || "密码".contains(msgContent)) {
//			String content="ngrok下载链接:http://pan.baidu.com/s/1dD99kGD 密码:jeyj";
//			renderOutTextMsg(content);
//		}else if ("授权".equalsIgnoreCase(msgContent)) {
//			String url=PropKit.get("domain")+"/oauth2/oauth";
//			String urlStr="<a href=\""+url+"\">点击我授权</a>";
//			renderOutTextMsg("授权地址"+urlStr);
//		}else if ("jssdk".equalsIgnoreCase(msgContent)) {
//			String url=PropKit.get("domain")+"/jssdk";
//			String urlStr="<a href=\""+url+"\">JSSDK</a>";
//			renderOutTextMsg("地址"+urlStr);
//		}
//		// 图文消息测试
//		else if ("news".equalsIgnoreCase(msgContent) || "新闻".equalsIgnoreCase(msgContent)) {
//			OutNewsMsg outMsg = new OutNewsMsg(inTextMsg);
//			outMsg.addNews("JFinal 2.0 发布,JAVA 极速 WEB+ORM 框架", "本星球第一个极速开发框架",
//					"https://mmbiz.qlogo.cn/mmbiz/KJoUl0sqZFS0fRW68poHoU3v9ulTWV8MgKIduxmzHiamkb3yHia8pCicWVMCaFRuGGMnVOPrrj2qM13u9oTahfQ9A/0?wx_fmt=png",
//					"http://mp.weixin.qq.com/s?__biz=MzA4NjM4Mjk2Mw==&mid=211063163&idx=1&sn=87d54e2992237a3f791f08b5cdab7990#rd");
//			outMsg.addNews("JFinal 1.8 发布,JAVA 极速 WEB+ORM 框架", "现在就加入 JFinal 极速开发世界，节省更多时间去跟女友游山玩水 ^_^",
//					"http://mmbiz.qpic.cn/mmbiz/zz3Q6WSrzq1ibBkhSA1BibMuMxLuHIvUfiaGsK7CC4kIzeh178IYSHbYQ5eg9tVxgEcbegAu22Qhwgl5IhZFWWXUw/0",
//					"http://mp.weixin.qq.com/s?__biz=MjM5ODAwOTU3Mg==&mid=200313981&idx=1&sn=3bc5547ba4beae12a3e8762ababc8175#rd");
//			outMsg.addNews("JFinal 1.6 发布,JAVA 极速 WEB+ORM 框架", "JFinal 1.6 主要升级了 ActiveRecord 插件，本次升级全面支持多数源、多方言、多缓",
//					"http://mmbiz.qpic.cn/mmbiz/zz3Q6WSrzq0fcR8VmNCgugHXv7gVlxI6w95RBlKLdKUTjhOZIHGSWsGvjvHqnBnjIWHsicfcXmXlwOWE6sb39kA/0",
//					"http://mp.weixin.qq.com/s?__biz=MjM5ODAwOTU3Mg==&mid=200121522&idx=1&sn=ee24f352e299b2859673b26ffa4a81f6#rd");
//			render(outMsg);
//		}
//		// 音乐消息测试
//		else if ("music".equalsIgnoreCase(msgContent) || "音乐".equals(msgContent)) {
//			OutMusicMsg outMsg = new OutMusicMsg(inTextMsg);
//			outMsg.setTitle("When The Stars Go Blue-Venke Knutson");
//			outMsg.setDescription("建议在 WIFI 环境下流畅欣赏此音乐");
//			outMsg.setMusicUrl("http://www.jfinal.com/When_The_Stars_Go_Blue-Venke_Knutson.mp3");
//			outMsg.setHqMusicUrl("http://www.jfinal.com/When_The_Stars_Go_Blue-Venke_Knutson.mp3");
//			outMsg.setFuncFlag(true);
//			render(outMsg);
//		} else if ("美女".equalsIgnoreCase(msgContent)) {
//			OutNewsMsg outMsg = new OutNewsMsg(inTextMsg);
//			outMsg.addNews("JFinal 宝贝更新喽", "jfinal 宝贝更新喽，我们只看美女 ^_^",
//					"https://mmbiz.qlogo.cn/mmbiz/KJoUl0sqZFRHa3VrmibqAXRfYPNdiamFnpPTOvXoxsFlRoOHbVibGhmHOEUQiboD3qXWszKuzWpibFxsVW1RmNB9hPw/0?wx_fmt=jpeg",
//					"http://mp.weixin.qq.com/s?__biz=MzA4NjM4Mjk2Mw==&mid=211356950&idx=1&sn=6315a1a2848aa8cb0694bf1f4accfb07#rd");
//			// outMsg.addNews("秀色可餐", "JFinal Weixin 极速开发就是这么爽，有木有 ^_^",
//			// "http://mmbiz.qpic.cn/mmbiz/zz3Q6WSrzq2GJLC60ECD7rE7n1cvKWRNFvOyib4KGdic3N5APUWf4ia3LLPxJrtyIYRx93aPNkDtib3ADvdaBXmZJg/0",
//			// "http://mp.weixin.qq.com/s?__biz=MjM5ODAwOTU3Mg==&mid=200987822&idx=1&sn=7eb2918275fb0fa7b520768854fb7b80#rd");
//
//			render(outMsg);
//		} else if ("视频教程".equalsIgnoreCase(msgContent) || "视频".equalsIgnoreCase(msgContent)) {
//			renderOutTextMsg("\thttp://pan.baidu.com/s/1nt2zAT7  \t密码:824r");
//		}	else if ("模板消息".equalsIgnoreCase(msgContent)) {
//
//			DataItem dataItem=new DataItem();
//			dataItem.setFirst(new TempItem("您好,Javen,欢迎使用模版消息!!", "#743A3A"));
//			dataItem.setProduct(new TempItem("微信公众平台测试", "#FF0000"));
//			dataItem.setPrice(new TempItem("39.8元", "#c4c400"));
//			SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
//			String time=sdf.format(new Date());
//			dataItem.setTime(new TempItem(time, "#0000FF"));
//			dataItem.setRemark(new TempItem("您的订单已提交，我们将尽快发货，祝生活愉快! 点击可以查看详细信息", "#008000"));
//
//			String jsonStr=TempToJson.getTempJson(inTextMsg.getFromUserName(), "tz1w-1fSN_Rzgj9_PjYuLU1EujvjPbtr1LL-_NtO6IM",
//					"#743A3A", "http://img2.3lian.com/2014/f5/158/d/86.jpg", dataItem);
//			System.out.println(inTextMsg.getToUserName()+" "+inTextMsg.getFromUserName());
//			 ApiResult result=TemplateMsgApi.send(jsonStr);
//			 System.out.println(result.getJson());
//			 renderNull();
//
//		}else if (msgContent.startsWith("百科")) {
//			msgContent=msgContent.replaceAll("^百科"+Regex,"");
//			SearchObject searchObject=new SearchObject();
//			searchObject.setQuery(msgContent);
//			searchObject.setCategory("baike");
//			searchObject.setCity("中国");
//			searchObject.setAppid(ApiConfigKit.getApiConfig().getAppId());
//			searchObject.setUid(inTextMsg.getFromUserName());
//			String jsonStr=JsonUtils.toJson(searchObject);
//			ApiResult apiResult = SemanticApi.search(jsonStr);
//			renderOutTextMsg(apiResult.getJson());
//		}
//		// 其它文本消息直接返回原值 + 帮助提示
//		else {
//			renderOutTextMsg("\t文本消息已成功接收，内容为： " + inTextMsg.getContent() + "\n\n" + helpStr);
//		}
	}

	/**
	 * 实现父类抽方法，处理图片消息
	 */
	protected void processInImageMsg(InImageMsg inImageMsg) {
//		OutImageMsg outMsg = new OutImageMsg(inImageMsg);
//		// 将刚发过来的图片再发回去
//		outMsg.setMediaId(inImageMsg.getMediaId());
//		render(outMsg);
		String picUrl =inImageMsg.getPicUrl();
		String respContent=FaceService.detect(picUrl);
		renderOutTextMsg(respContent);
	}

	/**
	 * 实现父类抽方法，处理语音消息
	 */
	protected void processInVoiceMsg(InVoiceMsg inVoiceMsg) {
		OutVoiceMsg outMsg = new OutVoiceMsg(inVoiceMsg);
		// 将刚发过来的语音再发回去
		outMsg.setMediaId(inVoiceMsg.getMediaId());
		render(outMsg);
	}

	/**
	 * 实现父类抽方法，处理视频消息
	 */
	protected void processInVideoMsg(InVideoMsg inVideoMsg) {
		/*
		 * 腾讯 api 有 bug，无法回复视频消息，暂时回复文本消息代码测试 OutVideoMsg outMsg = new
		 * OutVideoMsg(inVideoMsg); outMsg.setTitle("OutVideoMsg 发送");
		 * outMsg.setDescription("刚刚发来的视频再发回去"); // 将刚发过来的视频再发回去，经测试证明是腾讯官方的 api
		 * 有 bug，待 api bug 却除后再试 outMsg.setMediaId(inVideoMsg.getMediaId());
		 * render(outMsg);
		 */
		OutTextMsg outMsg = new OutTextMsg(inVideoMsg);
		outMsg.setContent("\t视频消息已成功接收，该视频的 mediaId 为: " + inVideoMsg.getMediaId());
		render(outMsg);
	}

	@Override
	protected void processInShortVideoMsg(InShortVideoMsg inShortVideoMsg) {
		OutTextMsg outMsg = new OutTextMsg(inShortVideoMsg);
		outMsg.setContent("\t视频消息已成功接收，该视频的 mediaId 为: " + inShortVideoMsg.getMediaId());
		render(outMsg);
	}

	/**
	 * 实现父类抽方法，处理地址位置消息
	 */
	protected void processInLocationMsg(InLocationMsg inLocationMsg) {
//		OutTextMsg outMsg = new OutTextMsg(inLocationMsg);
//		outMsg.setContent("已收到地理位置消息:" + "\nlocation_X = " + inLocationMsg.getLocation_X() + "\nlocation_Y = "
//				+ inLocationMsg.getLocation_Y() + "\nscale = " + inLocationMsg.getScale() + "\nlabel = "
//				+ inLocationMsg.getLabel());
//		render(outMsg);
		
		String Location_X = inLocationMsg.getLocation_X();
		String Location_Y = inLocationMsg.getLocation_Y();
		System.out.println("Location_X:" + Location_X + " Location_Y:"
				+ Location_Y);
		location=Location_Y+","+Location_X;
		
		String respContent="";
		if (StrKit.isBlank(nearbyContent) && StrKit.isBlank(weahterContent)) {
			respContent = "您发送的是地理位置消息！\n\n 1、查询天气 直接回复【天气】\n2、查询附近 如：附近酒店";
			renderOutTextMsg(respContent);
		}else {
			if (!StrKit.isBlank(nearbyContent)) {
				List<News> ambitusService = BaiduAmbitus.getAmbitusService(nearbyContent, location);
				if (ambitusService.size()>0) {
					OutNewsMsg outMsg = new OutNewsMsg(inLocationMsg);
					outMsg.addNews(ambitusService);
					render(outMsg);
					nearbyContent=null;
					location=null;
					return ;
				}else {
					respContent="\ue252 查询周边失败，请检查。";
					renderOutTextMsg(respContent);
				}
			}else if (!StrKit.isBlank(weahterContent)) {
				respContent=BaiduWeatherService.getWeatherService(location);
				weahterContent=null;
				location=null;
				renderOutTextMsg(respContent);
			}
			
		}
	}

	@Override
	protected void processInQrCodeEvent(InQrCodeEvent inQrCodeEvent) {
		if (InQrCodeEvent.EVENT_INQRCODE_SUBSCRIBE.equals(inQrCodeEvent.getEvent())) {
			logger.debug("扫码未关注：" + inQrCodeEvent.getFromUserName());
			OutTextMsg outMsg = new OutTextMsg(inQrCodeEvent);
			outMsg.setContent("感谢您的关注，二维码内容：" + inQrCodeEvent.getEventKey());
			render(outMsg);
		}
		if (InQrCodeEvent.EVENT_INQRCODE_SCAN.equals(inQrCodeEvent.getEvent())) {
			logger.debug("扫码已关注：" + inQrCodeEvent.getFromUserName());
			String key = inQrCodeEvent.getEventKey();
			renderOutTextMsg(key);
		}
	}

	@Override
	protected void processInLocationEvent(InLocationEvent inLocationEvent) {
		logger.debug("发送地理位置事件：" + inLocationEvent.getFromUserName());
		OutTextMsg outMsg = new OutTextMsg(inLocationEvent);
		outMsg.setContent("地理位置是：\n" + inLocationEvent.getLatitude()+"\n"+inLocationEvent.getLongitude());
		render(outMsg);
	}

	@Override
	protected void processInMassEvent(InMassEvent inMassEvent) {
		logger.debug("测试方法：processInMassEvent()");
		renderNull();
	}

	/**
	 * 实现父类抽方法，处理自定义菜单事件
	 */
	protected void processInMenuEvent(InMenuEvent inMenuEvent) {
		logger.debug("菜单事件：" + inMenuEvent.getFromUserName());
		OutTextMsg outMsg = new OutTextMsg(inMenuEvent);
		outMsg.setContent("菜单事件内容是：" + inMenuEvent.getEventKey());
		render(outMsg);
	}

	@Override
	protected void processInSpeechRecognitionResults(InSpeechRecognitionResults inSpeechRecognitionResults) {
		logger.debug("语音识别事件：" + inSpeechRecognitionResults.getFromUserName());
		OutTextMsg outMsg = new OutTextMsg(inSpeechRecognitionResults);
		outMsg.setContent("语音识别内容是：" + inSpeechRecognitionResults.getRecognition());
		render(outMsg);
	}

	/**
	 * 实现父类抽方法，处理链接消息 特别注意：测试时需要发送我的收藏中的曾经收藏过的图文消息，直接发送链接地址会当做文本消息来发送
	 */
	protected void processInLinkMsg(InLinkMsg inLinkMsg) {
		OutNewsMsg outMsg = new OutNewsMsg(inLinkMsg);
		outMsg.addNews("链接消息已成功接收", "链接使用图文消息的方式发回给你，还可以使用文本方式发回。点击图文消息可跳转到链接地址页面，是不是很好玩 :)",
				"http://mmbiz.qpic.cn/mmbiz/zz3Q6WSrzq1ibBkhSA1BibMuMxLuHIvUfiaGsK7CC4kIzeh178IYSHbYQ5eg9tVxgEcbegAu22Qhwgl5IhZFWWXUw/0",
				inLinkMsg.getUrl());
		render(outMsg);
	}

	@Override
	protected void processInCustomEvent(InCustomEvent inCustomEvent) {
		System.out.println("processInCustomEvent() 方法测试成功");
	}

	/**
	 * 实现父类抽方法，处理关注/取消关注消息
	 */
	protected void processInFollowEvent(InFollowEvent inFollowEvent) {
		OutTextMsg outMsg = new OutTextMsg(inFollowEvent);
		outMsg.setContent("感谢关注 JFinal Weixin 极速开发服务号，为您节约更多时间，去陪恋人、家人和朋友 :) \n\n\n " + helpStr);
		// 如果为取消关注事件，将无法接收到传回的信息
		render(outMsg);
	}

	// 处理接收到的模板消息是否送达成功通知事件
	protected void processInTemplateMsgEvent(InTemplateMsgEvent inTemplateMsgEvent) {
		String status = inTemplateMsgEvent.getStatus();
		renderOutTextMsg("模板消息是否接收成功：" + status);
	}

	@Override
	protected void processInShakearoundUserShakeEvent(InShakearoundUserShakeEvent inShakearoundUserShakeEvent) {
		logger.debug("摇一摇周边设备信息通知事件：" + inShakearoundUserShakeEvent.getFromUserName());
		OutTextMsg outMsg = new OutTextMsg(inShakearoundUserShakeEvent);
		outMsg.setContent("摇一摇周边设备信息通知事件UUID：" + inShakearoundUserShakeEvent.getUuid());
		render(outMsg);
	}

	@Override
	protected void processInVerifySuccessEvent(InVerifySuccessEvent inVerifySuccessEvent) {
		logger.debug("资质认证成功通知事件：" + inVerifySuccessEvent.getFromUserName());
		OutTextMsg outMsg = new OutTextMsg(inVerifySuccessEvent);
		outMsg.setContent("资质认证成功通知事件：" + inVerifySuccessEvent.getExpiredTime());
		render(outMsg);
	}

	@Override
	protected void processInVerifyFailEvent(InVerifyFailEvent inVerifyFailEvent) {
		logger.debug("资质认证失败通知事件：" + inVerifyFailEvent.getFromUserName());
		OutTextMsg outMsg = new OutTextMsg(inVerifyFailEvent);
		outMsg.setContent("资质认证失败通知事件：" + inVerifyFailEvent.getFailReason());
		render(outMsg);
	}
}
