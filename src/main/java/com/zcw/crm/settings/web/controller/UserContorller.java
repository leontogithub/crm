package com.zcw.crm.settings.web.controller;

import com.zcw.crm.settings.domain.User;
import com.zcw.crm.settings.service.UserService;
import com.zcw.crm.settings.service.impl.UserServiceImpl;
import com.zcw.crm.utils.MD5Util;
import com.zcw.crm.utils.PrintJson;
import com.zcw.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@WebServlet({"/settings/user/login.do"})
public class UserContorller extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        System.out.println(path);

        //对登录验证和登录页面进行放行
        if ("/settings/user/login.do".equals(path)) {
            login(request, response);
        }

    }

    private void login(HttpServletRequest request, HttpServletResponse response) {
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");

        //将密码的明文形式转换为DM5的密文形式
        loginPwd = MD5Util.getMD5(loginPwd);
        //接收ip地址
        String ip = request.getRemoteAddr();
        System.out.println("------>" + ip.toString());

        //代理类形态对象接口
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        try {
            User user = us.login(loginAct,loginPwd,ip);
            request.getSession().setAttribute("user",user);

            //执行到此处，业务层没有抛出任何异常
            PrintJson.printJsonFlag(response,true);

        }catch (Exception e){
            e.printStackTrace();
            //业务层抛出异常
            //登录失败,vo,map解决方案

            Map<String,Object> map = new HashMap<>();
            map.put("seccess",false);
            map.put("msg",e.getMessage());
            PrintJson.printJsonObj(response,map);
        }

    }
}
