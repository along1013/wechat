/**
  * Copyright (c) 2015-2016, Javen Zhou  (javenlife@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.jfinal.weixin.demo;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.render.ViewType;
import com.jfinal.weixin.dao.SmsInfo;
import com.jfinal.weixin.dao.UserInfo;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.usercontroller.UserController;
import com.jfinal.weixin.weboauth2.RedirectUri;
/**
 * @author Javen
 * 2016年1月15日
 */
public class WeixinConfig extends JFinalConfig {
	
	/**
	 * 如果生产环境配置文件存在，则优先加载该配置，否则加载开发环境配置文件
	 * @param pro 生产环境配置文件
	 * @param dev 开发环境配置文件
	 */
	public void loadProp(String pro, String dev) {
		try {
			PropKit.use(pro);
		}
		catch (Exception e) {
			PropKit.use(dev);
		}
	}
	
	public void configConstant(Constants me) {
		loadProp("a_little_config_pro.txt", "a_little_config.txt");
		me.setDevMode(PropKit.getBoolean("devMode", false));
		me.setEncoding("utf-8");
		me.setViewType(ViewType.JSP);
		// ApiConfigKit 设为开发模式可以在开发阶段输出请求交互的 xml 与 json 数据
		ApiConfigKit.setDevMode(me.getDevMode());
	}
	
	public void configRoute(Routes me) {
		me.add("/msg", WeixinMsgController.class);
		me.add("/api", WeixinApiController.class, "/api");
//		me.add("/pay", WeixinPayController.class);
		me.add("/checkState", IndexController.class);
		me.add("/oauth2",RedirectUri.class);
		me.add("/user",UserController.class);
//		me.add("/jssdk",ShareController.class,"_front");

	}
	
	public void configPlugin(Plugins me) {
		 C3p0Plugin c3p0Plugin = new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
		 me.add(c3p0Plugin);

		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		me.add(arp);

		arp.addMapping("user_info", UserInfo.class);
		arp.addMapping("sms_info",SmsInfo.class);
		// EhCachePlugin ecp = new EhCachePlugin();
		// me.add(ecp);
		
		// RedisPlugin redisPlugin = new RedisPlugin("weixin", "127.0.0.1");
		// me.add(redisPlugin);
	}
	
	public void configInterceptor(Interceptors me) {
		
	}
	
	public void configHandler(Handlers me) {
		
	}
	
	public void afterJFinalStart() {
		// 1.5 之后支持redis存储access_token、js_ticket，需要先启动RedisPlugin
//		ApiConfigKit.setAccessTokenCache(new RedisAccessTokenCache());
		// 1.6新增的2种初始化
//		ApiConfigKit.setAccessTokenCache(new RedisAccessTokenCache(Redis.use("weixin")));
//		ApiConfigKit.setAccessTokenCache(new RedisAccessTokenCache("weixin"));
	}

	public static void main(String[] args) {
		JFinal.start("webapp", 8080, "/", 5);
	}
	
}
