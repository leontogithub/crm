package com.zcw.crm.workbench.web.controller;

import com.zcw.crm.settings.domain.User;
import com.zcw.crm.settings.service.UserService;
import com.zcw.crm.settings.service.impl.UserServiceImpl;
import com.zcw.crm.utils.DateTimeUtil;
import com.zcw.crm.utils.PrintJson;
import com.zcw.crm.utils.ServiceFactory;
import com.zcw.crm.utils.UUIDUtil;
import com.zcw.crm.vo.PaginvationVo;
import com.zcw.crm.workbench.domain.Activity;
import com.zcw.crm.workbench.domain.ActivityRemark;
import com.zcw.crm.workbench.service.ActivityService;
import com.zcw.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * 市场活动控制器
 */

@WebServlet({"/workbench/activity/getUserList.do", "/workbench/activity/save.do",
        "/workbench/activity/pageList.do", "/workbench/activity/delete.do",
        "/workbench/activity/getUserListAndActivity.do", "/workbench/activity/update.do",
        "/workbench/activity/detail.do", "/workbench/activity/getRemarkListByAid.do",
        "/workbench/activity/deleteRemark.do", "/workbench/activity/saveRemark.do",
        "/workbench/activity/updateRemark.do"})
public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/workbench/activity/getUserList.do".equals(path)) {
            getUserList(request, response);
        } else if ("/workbench/activity/save.do".equals(path)) {
            save(request, response);
        } else if ("/workbench/activity/pageList.do".equals(path)) {
            pageList(request, response);
        } else if ("/workbench/activity/delete.do".equals(path)) {
            delete(request, response);
        } else if ("/workbench/activity/update.do".equals(path)) {
            update(request, response);
        } else if ("/workbench/activity/getUserListAndActivity.do".equals(path)) {
            getUserListAndActivity(request, response);
        } else if ("/workbench/activity/detail.do".equals(path)) {
            detail(request, response);
        } else if ("/workbench/activity/getRemarkListByAid.do".equals(path)) {
            getRemarkListByAid(request, response);
        } else if ("/workbench/activity/deleteRemark.do".equals(path)) {
            deleteRemark(request, response);
        } else if ("/workbench/activity/saveRemark.do".equals(path)) {
            saveRemark(request, response);
        } else if ("/workbench/activity/updateRemark.do".equals(path)) {
            updateRemark(request, response);
        }


    }


    /**
     * 修改备注
     * @param request
     * @param response
     */
    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("更新备注");
        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "1";

        System.out.println(noteContent);
        System.out.println(editBy);

        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setEditTime(editTime);
        ar.setEditBy(editBy);
        ar.setEditFlag(editFlag);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.updateRemark(ar);

        Map<String,Object> map = new HashMap<>();
        map.put("ar",ar);
        map.put("success",flag);

        System.out.println(flag);

        PrintJson.printJsonObj(response,map);


    }


    /**
     * 保存新增备注
     *
     * @param request
     * @param response
     */
    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("保存新增备注");

        String activityId = request.getParameter("activityId");
        String noteContent = request.getParameter("noteContent");

        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();

        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String editFlag = "0";

        ActivityRemark ar = new ActivityRemark();
        ar.setActivityId(activityId);
        ar.setNoteContent(noteContent);
        ar.setId(id);
        ar.setCreateTime(createTime);
        ar.setCreateBy(createBy);
        ar.setEditFlag(editFlag);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.saveRemark(ar);
        Map<String,Object> map = new HashMap<>();
        map.put("ar",ar);
        map.put("success",flag);
        PrintJson.printJsonObj(response, map);
    }


    /**
     * 删除市场活动备注
     *
     * @param request
     * @param response
     */
    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("删除市场活动备注");
        String id = request.getParameter("id");
        System.out.println(id);
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.deleteRemark(id);
        PrintJson.printJsonFlag(response, flag);

    }


    /**
     * 市场活动备注查询
     *
     * @param request
     * @param response
     */
    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("市场活动备注");
        String activityId = request.getParameter("activityId");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<ActivityRemark> remarks = as.getRemarkListByAid(activityId);
        System.out.println(remarks);
        PrintJson.printJsonObj(response, remarks);
    }


    /**
     * 市场活动信息详情页面
     *
     * @param request
     * @param response
     */
    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("市场活动信息详情页面");
        String id = request.getParameter("id");
        ActivityService activityService = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Activity activity = activityService.detail(id);
        request.setAttribute("a", activity);
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request, response);
    }


    /**
     * 市场活动修改
     *
     * @param request
     * @param response
     */
    private void update(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("更新市场活动列表");

        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");

        System.out.println(id + "," + owner);

        //修改人,当前登录用户
        String editBy = ((User) request.getSession().getAttribute("user")).getName();
        //修改时间
        String editTime = DateTimeUtil.getSysTime();

        Activity a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setEditBy(editBy);
        a.setEditTime(editTime);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.update(a);

        PrintJson.printJsonFlag(response, flag);

    }

    /**
     * 市场活动列表修改前的数据查询
     *
     * @param request
     * @param response
     */
    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("修改活动列表中显示的信息");
        String id = request.getParameter("id");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Map<String, Object> map = as.getUserListAndActivity(id);
        Set<String> set = map.keySet();
        Activity a = (Activity) map.get("a");
        System.out.println(a.getOwner());
        PrintJson.printJsonObj(response, map);
    }


    /**
     * 市场活动列表的删除
     *
     * @param request
     * @param response
     */
    private void delete(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("市场活动列表删除");
        String[] ids = request.getParameterValues("id");
        System.out.println(Arrays.toString(ids));
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.delete(ids);
        PrintJson.printJsonFlag(response, flag);


    }


    /**
     * 市场活动列表查询
     *
     * @param request
     * @param response
     */
    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("市场活动列表查询");

        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");

        String pageNoStr = request.getParameter("pageNo");
        Integer pageNo = Integer.valueOf(pageNoStr);
        String pageSizeStr = request.getParameter("pageSize");
        Integer pageSize = Integer.valueOf(pageSizeStr);
        //m
        int skipCount = (pageNo - 1) * pageSize;

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("owner", owner);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("pageSize", pageSize);
        map.put("skipCount", skipCount);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        PaginvationVo<Activity> vo = as.pageList(map);
        PrintJson.printJsonObj(response, vo);


        //计算

    }

    /**
     * 市场活动添加
     *
     * @param request
     * @param response
     */
    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("市场活动添加操作");
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        //创建时间，当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        //创建人，当前登录用户
//        System.out.println(request.getSession().getAttribute("user"));
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
//        System.out.println(createBy);

        Activity a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setCreateTime(createTime);
        a.setCreateBy(createBy);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.save(a);

        PrintJson.printJsonFlag(response, flag);

    }


    /**
     * 取得用户信息列表
     *
     * @param request
     * @param response
     */
    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        //取得用户信息列表
        System.out.println("用户信息列表");
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> users = us.getUserLiset();
        for (User user : users) {
            System.out.println(user.getName());
        }
        PrintJson.printJsonObj(response, users);

    }


}
