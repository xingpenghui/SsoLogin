package com.feri.ssologin.service;

import com.feri.common.vo.ResultVo;

/**
 *@Author feri
 *@Date Created in 2019/3/19 14:19
 */
public interface LoginService {

    //登录
    ResultVo login(String phone,String password,String ip);
    //校验
    ResultVo check(String token);
    //退出
    ResultVo exit(String token);
}
