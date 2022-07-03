package com.zcw.crm.settings.service.impl;

import com.zcw.crm.exception.LoginException;
import com.zcw.crm.settings.dao.UserDao;
import com.zcw.crm.settings.domain.User;
import com.zcw.crm.settings.service.UserService;
import com.zcw.crm.utils.DateTimeUtil;
import com.zcw.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServiceImpl implements UserService {
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public User login(String loginAct, String loginPwd, String ip) throws LoginException {
        Map<String, String> map = new HashMap<>();
        map.put("loginAct", loginAct);
        map.put("loginPwd", loginPwd);
        User user = userDao.login(map);
        if (user == null) {
            throw new LoginException("账户密码错误");
        }


        //程序到此处，账户密码错误
        //验证失效时间
        String expireTime = user.getExpireTime();
        String currentTime = DateTimeUtil.getSysTime();
        int pare = currentTime.compareTo(expireTime);
        if (pare > 0) {
            //账户失效
            throw new LoginException("当前账号失效");
        }

        //判断锁定状态
        if ("0".equals(user.getLockState())) {
            throw new LoginException("账号被锁定");
        }

        //判断ip地址
        if (!user.getAllowIps().contains(ip)) {
            throw new LoginException("ip地址无效");
        }

        return user;
    }

    @Override
    public List<User> getUserLiset() {
        List<User> users = userDao.getUserList();
        return users;
    }

}
