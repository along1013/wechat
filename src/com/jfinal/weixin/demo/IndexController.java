/**
 * Copyright (c) 2015-2016, Javen Zhou  (javenlife@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.jfinal.weixin.demo;

import com.jfinal.core.Controller;
import com.jfinal.weixin.dao.UserInfo;

/**
 * @author Javen
 * 2016年1月15日
 */
public class IndexController extends Controller {
	public void index(){
		renderText("success");
	}

	public void test(){
		UserInfo userInfo = UserInfo.dao.findById(1);
		System.out.println(userInfo.get("name"));
		renderText(userInfo.getStr("name"));
	}
}
