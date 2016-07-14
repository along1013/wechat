package com.jfinal.weixin.dao;

import com.jfinal.plugin.activerecord.Model;

/**
 * Created by yaoyunlong on 2016/7/14 0014.
 */
public class UserInfo extends Model<UserInfo> {
    public static final UserInfo dao = new UserInfo();

    //根据手机号码查询密码
    public String getPwdByPhone(String phone){
        return dao.findByIdLoadColumns(phone,"pwd").get("pwd");
    }

}
