package com.feri.ssologin.controller;

import com.feri.common.util.ResultUtil;
import com.feri.common.vo.ResultVo;
import com.feri.ssologin.config.SystemConst;
import com.feri.ssologin.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *@Author feri
 *@Date Created in 2019/3/19 10:14
 */
@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    //登录接口
    @PostMapping("userlogin.do")
    public ResultVo login(String phone, String pass, HttpServletRequest request, HttpServletResponse response){
        Cookie[] cks=request.getCookies();
        String token="";
        ResultVo resultVo=null;
        for(Cookie c:cks){
            if(c.getName().equals(SystemConst.COOKIETOKEN)){
                token=c.getValue();
                break;
            }
        }
        if(token.length()>0){
            resultVo=loginService.check(token);
        }else{
            //没有过Token
            resultVo=loginService.login(phone,pass,request.getRemoteAddr());
            if(resultVo.getCode()==1){
                Cookie cookie=new Cookie(SystemConst.COOKIETOKEN,resultVo.getData().toString());
                cookie.setPath("/");
                cookie.setDomain("");
                response.addCookie(cookie);
            }
        }
        return resultVo;
    }
    //注销接口
    @GetMapping("userexit.do")
    public ResultVo exit(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cks=request.getCookies();
        String token="";
        ResultVo resultVo=null;
        for(Cookie c:cks){
            if(c.getName().equals(SystemConst.COOKIETOKEN)){
                token=c.getValue();
                break;
            }
        }
        if(token.length()>0){
            //删除Redis
            //删除Cookie
            loginService.exit(token);
            Cookie cookie=new Cookie(SystemConst.COOKIETOKEN,"");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        return ResultUtil.exec(true,"OK",null);
    }
    //校验是否登录有效性
    @GetMapping("usercheck.do")
    public ResultVo check(HttpServletRequest request){
        Cookie[] cks=request.getCookies();
        String token="";
        ResultVo resultVo=null;
        for(Cookie c:cks){
            if(c.getName().equals(SystemConst.COOKIETOKEN)){
                token=c.getValue();
                break;
            }
        }
        if(token.length()>0){
            resultVo=loginService.check(token);
        }else {
            resultVo=ResultUtil.exec(false,"登录无效",null);
        }
        return resultVo;
    }
}
