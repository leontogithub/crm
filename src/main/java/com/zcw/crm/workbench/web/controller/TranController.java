package com.zcw.crm.workbench.web.controller;

import com.zcw.crm.settings.domain.User;
import com.zcw.crm.settings.service.UserService;
import com.zcw.crm.settings.service.impl.UserServiceImpl;
import com.zcw.crm.utils.DateTimeUtil;
import com.zcw.crm.utils.PrintJson;
import com.zcw.crm.utils.ServiceFactory;
import com.zcw.crm.utils.UUIDUtil;
import com.zcw.crm.workbench.dao.ClueDao;
import com.zcw.crm.workbench.domain.Activity;
import com.zcw.crm.workbench.domain.Contacts;
import com.zcw.crm.workbench.domain.Tran;
import com.zcw.crm.workbench.domain.TranHistory;
import com.zcw.crm.workbench.service.ActivityService;
import com.zcw.crm.workbench.service.CustomerService;
import com.zcw.crm.workbench.service.TranService;
import com.zcw.crm.workbench.service.impl.ActivityServiceImpl;
import com.zcw.crm.workbench.service.impl.ClueServiceImpl;
import com.zcw.crm.workbench.service.impl.CustomerServiceImpl;
import com.zcw.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.ServletContext;
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
 * 交易控制器
 */

@WebServlet({"/workbench/transaction/add.do", "/workbench/transaction/searchActivityListByName.do",
        "/workbench/transaction/searchContactsByFullname.do", "/workbench/transaction/getCustomerName.do",
        "/workbench/transaction/save.do", "/workbench/transaction/showTranList.do",
        "/workbench/transaction/detail.do", "/workbench/transaction/getHistoryListByTranId.do",
        "/workbench/transaction/changeStage.do", "/workbench/transaction/getCharts.do"})
public class TranController extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/workbench/transaction/add.do".equals(path)) {
            add(request, response);
        } else if ("/workbench/transaction/searchActivityListByName.do".equals(path)) {
            searchActivityListByName(request, response);
        } else if ("/workbench/transaction/searchContactsByFullname.do".equals(path)) {
            searchContactsByFullname(request, response);
        } else if ("/workbench/transaction/getCustomerName.do".equals(path)) {
            getCustomerName(request, response);
        } else if ("/workbench/transaction/save.do".equals(path)) {
            save(request, response);
        } else if ("/workbench/transaction/showTranList.do".equals(path)) {
            showTranList(request, response);
        } else if ("/workbench/transaction/detail.do".equals(path)) {
            detail(request, response);
        } else if ("/workbench/transaction/getHistoryListByTranId.do".equals(path)) {
            getHistoryListByTranId(request, response);
        } else if ("/workbench/transaction/changeStage.do".equals(path)) {
            changeStage(request, response);
        } else if ("/workbench/transaction/getCharts.do".equals(path)) {
            getCharts(request, response);
        }
    }


    /**
     * 交易阶段数量统计图表数据
     *
     * @param request
     * @param response
     */
    private void getCharts(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("交易阶段数量统计图表数据");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Map<String,Object> map = ts.getCharts();

        PrintJson.printJsonObj(response,map);

    }


    /**
     * 更改交易阶段
     *
     * @param request
     * @param response
     */
    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("更改交易阶段");
        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");

        String editBy = ((User) request.getSession().getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();

        Tran t = new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setEditBy(editBy);
        t.setEditTime(editTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        Boolean flag = ts.changeStage(t);

        ServletContext application = request.getServletContext();
        Map<String, String> pMap = (Map<String, String>) application.getAttribute("pMap");
        String possibility = pMap.get(stage);
        System.out.println(possibility);
        t.setPossibility(possibility);
        Map<String, Object> map = new HashMap<>();
        map.put("success", flag);
        map.put("t", t);

        PrintJson.printJsonObj(response, map);


    }


    /**
     * 通过tranId得到交易历史列表
     *
     * @param request
     * @param response
     */
    private void getHistoryListByTranId(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("交易历史列表");
        String tranId = request.getParameter("tranId");

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> tHistories = ts.getHistoryListByTranId(tranId);

        ServletContext sc = request.getServletContext();

        Map<String, String> map = (Map<String, String>) sc.getAttribute("pMap");
        for (TranHistory tHistory : tHistories) {
            String stage = tHistory.getStage();
            String possibility = map.get(stage);
            tHistory.setPossibility(possibility);
        }


        PrintJson.printJsonObj(response, tHistories);

    }

    /**
     * 交易详细信息页
     *
     * @param request
     * @param response
     */
    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("交易详细信息页");
        String id = request.getParameter("id");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        Tran tran = ts.detail(id);

        //处理可能性
        String stage = tran.getStage();
        ServletContext application = request.getServletContext();
//        ServletContext application = this.getServletContext();
//        ServletContext application = this.getServletConfig().getServletContext();

        Map<String, String> pMap = (Map<String, String>) application.getAttribute("pMap");
        String possibility = pMap.get(stage);
//        System.out.println(possibility);


        request.setAttribute("t", tran);
        request.setAttribute("possibility", possibility);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request, response);

    }


    /**
     * 交易列表
     *
     * @param request
     * @param response
     */
    private void showTranList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("交易列表");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<Tran> trans = ts.showTranList();

        PrintJson.printJsonObj(response, trans);

    }


    /**
     * 创建交易
     *
     * @param request
     * @param response
     */
    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("创建交易");

        String id = UUIDUtil.getUUID();

        String customerName = request.getParameter("customerName");
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();

        Tran tran = new Tran();
        tran.setId(id);
        tran.setOwner(owner);
        tran.setMoney(money);
        tran.setName(name);
        tran.setExpectedDate(expectedDate);
        tran.setStage(stage);
        tran.setType(type);
        tran.setSource(source);
        tran.setActivityId(activityId);
        tran.setContactsId(contactsId);
        tran.setDescription(description);
        tran.setContactSummary(contactSummary);
        tran.setNextContactTime(nextContactTime);
        tran.setCreateBy(createBy);
        tran.setCreateTime(createTime);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());

        boolean flag = ts.save(tran, customerName);

        if (flag) {
            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");
        }


    }


    /**
     * 客户名称自动补全
     *
     * @param request
     * @param response
     */
    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("客户名称自动补全");
        String name = request.getParameter("name");
        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> list = cs.getCustomerName(name);

        for (String s : list) {
            System.out.println(s);
        }

        PrintJson.printJsonObj(response, list);
    }


    /**
     * 交易创建联系人查询
     *
     * @param request
     * @param response
     */
    private void searchContactsByFullname(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("创建交易联系人查询");
        String funllname = request.getParameter("funllname");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<Contacts> contacts = ts.searchContactsByFullname(funllname);

        PrintJson.printJsonObj(response, contacts);
    }


    /**
     * 交易创建中市场活动列表查询
     *
     * @param request
     * @param response
     */
    private void searchActivityListByName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("交易创建中市场活动列表查询");
        String name = request.getParameter("name");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> activities = as.getActivityListByName(name);

        PrintJson.printJsonObj(response, activities);
    }


    /**
     * 交易创建的所有者信息返回
     *
     * @param request
     * @param response
     */
    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("交易创建所有者信息返回");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> users = us.getUserLiset();

        request.setAttribute("users", users);

        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request, response);

    }
}
