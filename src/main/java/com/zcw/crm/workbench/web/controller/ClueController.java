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
import com.zcw.crm.workbench.domain.Clue;
import com.zcw.crm.workbench.domain.Tran;
import com.zcw.crm.workbench.service.ActivityService;
import com.zcw.crm.workbench.service.ClueService;
import com.zcw.crm.workbench.service.impl.ActivityServiceImpl;
import com.zcw.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 线索控制器
 */
@WebServlet({"/workbench/clue/getUserList.do", "/workbench/clue/save.do",
        "/workbench/clue/detail.do", "/workbench/clue/getActivityListByClueId.do",
        "/workbench/clue/unbund.do", "/workbench/clue/getActivityListByNameAndNotByClueId.do",
        "/workbench/clue/bund.do", "/workbench/clue/getActivityListByName.do",
        "/workbench/clue/getClueList.do", "/workbench/clue/convert.do"})
public class ClueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/workbench/clue/getUserList.do".equals(path)) {
            getUserList(request, response);
        } else if ("/workbench/clue/save.do".equals(path)) {
            save(request, response);
        } else if ("/workbench/clue/detail.do".equals(path)) {
            detail(request, response);
        } else if ("/workbench/clue/getActivityListByClueId.do".equals(path)) {
            getActivityListByClueId(request, response);
        } else if ("/workbench/clue/unbund.do".equals(path)) {
            unbund(request, response);
        } else if ("/workbench/clue/getActivityListByNameAndNotByClueId.do".equals(path)) {
            getActivityListByNameAndNotByClueId(request, response);
        } else if ("/workbench/clue/bund.do".equals(path)) {
            bund(request, response);
        } else if ("/workbench/clue/getActivityListByName.do".equals(path)) {
            getActivityListByName(request, response);
        } else if ("/workbench/clue/getClueList.do".equals(path)) {
            getClueList(request, response);
        } else if ("/workbench/clue/convert.do".equals(path)) {
            convert(request, response);
        }

    }


    /**
     * 转换线索
     *
     * @param request
     * @param response
     */
    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("转换线索");
        String clueId = request.getParameter("clueId");
//        接收标记，用来后台区分是否需要创建交易
        String flag = request.getParameter("flag");
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        Tran tran = null;

        if ("a".equals(flag)) {
            tran = new Tran();
            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectedDate = request.getParameter("expectedDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");
            String id = UUIDUtil.getUUID();
            String createTime = DateTimeUtil.getSysTime();

            tran.setId(id);
            tran.setMoney(money);
            tran.setName(name);
            tran.setExpectedDate(expectedDate);
            tran.setStage(stage);
            tran.setActivityId(activityId);
            tran.setCreateTime(createTime);
            tran.setCreateBy(createBy);

        }

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean flag1 = cs.convert(clueId, createBy, tran);
        if (flag1) {
            response.sendRedirect(request.getContextPath() + "/workbench/clue/index.jsp");
        }

    }


    /**
     * 分页查询线索
     * clueId
     *
     * @param request
     * @param response
     */

    private void getClueList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("查询线索列表");

        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");

        Integer pageNo = Integer.valueOf(pageNoStr);
        Integer pageSize = Integer.valueOf(pageSizeStr);

        String fullname = request.getParameter("fullname");
        String company = request.getParameter("company");
        String phone = request.getParameter("phone");
        String source = request.getParameter("source");
        String owner = request.getParameter("owner");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");

        //m
        Integer skipCount = pageSize * (pageNo - 1);

        Map<String, Object> map = new HashMap<>();
        map.put("pageSize", pageSize);
        map.put("skipCount", skipCount);
        map.put("fullname", fullname);
        map.put("company", company);
        map.put("phone", phone);
        map.put("source", source);
        map.put("owner", owner);
        map.put("mphone", mphone);
        map.put("state", state);

/*        System.out.println(skipCount);
        System.out.println(fullname);
        System.out.println(owner);
        System.out.println(state);*/


        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        PaginvationVo<Clue> pv = cs.getClueListVo(map);

/*        for (Clue clue : clues) {
            System.out.println(clue.getFullname());
        }*/

        PrintJson.printJsonObj(response, pv);

    }


    /**
     * 转换关联市场活动查询
     *
     * @param request
     * @param response
     */
    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("转换关联市场活动查询");
        String name = request.getParameter("name");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activities = as.getActivityListByName(name);
        PrintJson.printJsonObj(response, activities);

    }


    /**
     * 关联市场活动
     *
     * @param request
     * @param response
     */
    private void bund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("关联市场活动");
        String activityIds = request.getParameter("activityId");
        String clueId = request.getParameter("clueId");
        System.out.println(clueId);

        String[] activityId = activityIds.split("&");
        for (String s : activityId) {
            System.out.println(s);
        }


        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        boolean falg = cs.bund(activityId, clueId);


        PrintJson.printJsonFlag(response, falg);

    }


    /**
     * 查询关联的市场活动，查询的必须为未被关联的
     *
     * @param request
     * @param response
     */
    private void getActivityListByNameAndNotByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("查询关联市场活动");
        String name = request.getParameter("name");
        String clueId = request.getParameter("clueId");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("clueId", clueId);
        List<Activity> activities = as.getActivityListByNameAndNotByClueId(map);
        PrintJson.printJsonObj(response, activities);
    }


    /**
     * 线索与市场活动解除关联
     *
     * @param request
     * @param response
     */
    private void unbund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("线索与市场活动解除关联");
        String id = request.getParameter("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.unbund(id);
        System.out.println(flag);
        PrintJson.printJsonFlag(response, flag);
    }


    /**
     * 根据线索查询市场活动
     *
     * @param request
     * @param response
     */
    private void getActivityListByClueId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据线索查询市场活动");
        String clueId = request.getParameter("clueId");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activities = as.getActivityListByClueId(clueId);

        PrintJson.printJsonObj(response, activities);


    }


    /**
     * 查询线索详情
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("线索详情");
        String id = request.getParameter("id");
        System.out.println(id);

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue clue = cs.detail(id);

//        System.out.println(clue.getFullname());

        request.setAttribute("c", clue);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request, response);


    }


    /**
     * 创建线索
     *
     * @param request
     * @param response
     */
    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("保存线索");

        String id = UUIDUtil.getUUID();

        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");

        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();

        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        Clue clue = new Clue();
        clue.setId(id);
        clue.setAddress(address);
        clue.setAppellation(appellation);
        clue.setCompany(company);
        clue.setContactSummary(contactSummary);
        clue.setCreateBy(createBy);
        clue.setCreateTime(createTime);
        clue.setEmail(email);
        clue.setFullname(fullname);
        clue.setJob(job);
        clue.setMphone(mphone);
        clue.setOwner(owner);
        clue.setPhone(phone);
        clue.setWebsite(website);
        clue.setState(state);
        clue.setSource(source);
        clue.setDescription(description);
        clue.setNextContactTime(nextContactTime);

        boolean flag = cs.save(clue);

        PrintJson.printJsonFlag(response, flag);


    }


    /**
     * 线索创建的所有者下拉列表展现
     *
     * @param request
     * @param response
     */
    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("线索创建所有者展现");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> users = us.getUserLiset();
        PrintJson.printJsonObj(response, users);
    }


}
