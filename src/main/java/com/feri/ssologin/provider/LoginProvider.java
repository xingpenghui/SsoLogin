package com.feri.ssologin.provider;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.feri.common.util.JedisUtil;
import com.feri.common.util.PassUtil;
import com.feri.common.util.ResultUtil;
import com.feri.common.util.TokenUtil;
import com.feri.common.vo.ResultVo;
import com.feri.dao.user.UserMapper;
import com.feri.dao.user.UserlogMapper;
import com.feri.entity.user.User;
import com.feri.entity.user.Userlog;
import com.feri.ssologin.config.SystemConst;
import com.feri.ssologin.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Objects;

/**
 *@Author feri
 *@Date Created in 2019/3/19 14:19
 */
@Service
public class LoginProvider implements LoginService {
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserlogMapper userlogMapper;

    @Override
    public ResultVo login(String phone, String password, String ip) {
        //1、查询用户
        QueryWrapper<User> queryWrapper= new QueryWrapper<User>().eq("phone",phone);
        User user=userMapper.selectOne(queryWrapper);
        if(user!=null){
            //2、校验密码是否正确
            if(Objects.equals(user.getPassword(),password)){
                //登录成功
                //3、生成令牌
                String token=TokenUtil.createToken(user.getId(),phone);
                //4、存储到Redis中
                jedisUtil.save(SystemConst.PHONETOKEN,phone,token);
                jedisUtil.save(SystemConst.LOGINTOKEN,token,JSON.toJSONString(user));
                Userlog userlog=new Userlog();
                userlog.setContent("登录用户："+token);
                userlog.setCreatetime(new Date());
                userlog.setUid(user.getId());
                userlog.setIp(ip);
                userlogMapper.insert(userlog);
                //5、返回信息
                return ResultUtil.exec(true,"OK",token);
            }
        }
        return ResultUtil.exec(false,"用户名或密码不正确",null);
    }

    @Override
    public ResultVo check(String token) {
        if(jedisUtil.isHave(SystemConst.LOGINTOKEN,token)){
            return ResultUtil.exec(true,"OK",JSON.parseObject(jedisUtil.get(SystemConst.LOGINTOKEN,token),User.class));
        }
        return ResultUtil.exec(false,"登录失效",null);
    }

    @Override
    public ResultVo exit(String token) {
        if(jedisUtil.isHave(SystemConst.LOGINTOKEN,token)){
            //删除
            jedisUtil.del(SystemConst.LOGINTOKEN,token);
            String msg=PassUtil.base64Dec(token,"UTF-8");
            String[] arr=msg.split(",");
            jedisUtil.del(SystemConst.PHONETOKEN,arr[1]);
            Userlog userlog=new Userlog();
            userlog.setContent("用户退出");
            userlog.setCreatetime(new Date());
            userlog.setUid(Integer.parseInt(arr[0]));
            userlog.setIp("");
            userlogMapper.insert(userlog);
        }
        return null;
    }
}
