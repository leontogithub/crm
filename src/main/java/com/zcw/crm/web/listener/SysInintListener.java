package com.zcw.crm.web.listener;

import com.zcw.crm.settings.service.DicService;
import com.zcw.crm.settings.service.impl.DicServiceImpl;
import com.zcw.crm.utils.ServiceFactory;
import com.zcw.crm.workbench.service.ClueService;
import com.zcw.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.*;


@WebListener
public class SysInintListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
//        System.out.println("hhh");
        System.out.println("开始");
        ServletContext application = event.getServletContext();
        //取出数据字典
        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());
        Map<String, Object> map = ds.getAll();

        Set<String> set = map.keySet();
        for (String key : set) {
            application.setAttribute(key, map.get(key));
        }
        System.out.println("结束");

        //数据字典处理完毕后，处理stage2Possibility.properties文件，
        //解析文件，将文件键值对关系处理成java键值对关系
   /* ResourceBundle rb = ResourceBundle.getBundle("stage2Possibility");
    rb.get*/

        Map<String,String> map1 = new HashMap<>();

        ResourceBundle rb = ResourceBundle.getBundle("stage2Possibility");
        Enumeration<String> keys = rb.getKeys();
        while (keys.hasMoreElements()) {
            //阶段
            String s = keys.nextElement();
            //可能性
            String value = rb.getString(s);
            map1.put(s,value);
        }

        //将map保存到服务器缓存中
/*
        Set<String> set1 = map1.keySet();
        for (String s : set1) {
            System.out.println(s + "==" + map1.get(s));
        }

*/

        application.setAttribute("pMap",map1);


    }




    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
