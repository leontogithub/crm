package com.zcw.crm.workbench.service.impl;

import com.zcw.crm.utils.DateTimeUtil;
import com.zcw.crm.utils.SqlSessionUtil;
import com.zcw.crm.utils.UUIDUtil;
import com.zcw.crm.workbench.dao.ContactsDao;
import com.zcw.crm.workbench.dao.CustomerDao;
import com.zcw.crm.workbench.dao.TranDao;
import com.zcw.crm.workbench.dao.TranHistoryDao;
import com.zcw.crm.workbench.domain.Contacts;
import com.zcw.crm.workbench.domain.Customer;
import com.zcw.crm.workbench.domain.Tran;
import com.zcw.crm.workbench.domain.TranHistory;
import com.zcw.crm.workbench.service.TranService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TranServiceImpl implements TranService {
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    @Override
    public List<Contacts> searchContactsByFullname(String funllname) {
        List<Contacts> contacts = contactsDao.searchContactsByFullname(funllname);
        return contacts;
    }

    @Override
    public boolean save(Tran tran, String customerName) {
        /**
         * 交易添加
         */

        boolean flag = true;

        //tran对象里面没有customerid，customerid
//        根据customerName在客户表查询customerid,如果客户存在，则不需要创建新客户,反之需要创建客户，并获取新建客户的customerId
        Customer customer = customerDao.getCustomerByName(customerName);

        //为空创建客户
        if (customer == null) {
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setCreateBy(tran.getCreateBy());
            customer.setCreateTime(DateTimeUtil.getSysTime());
            customer.setContactSummary(tran.getContactSummary());
            customer.setNextContactTime(tran.getNextContactTime());
            customer.setOwner(tran.getOwner());

            //添加客户
            int count1 = customerDao.save(customer);
            if (count1 != 1) {
                flag = false;
            }
        }

        //执行添加交易操作
        tran.setCustomerId(customer.getId());
        int count2 = tranDao.save(tran);
        if (count2 != 1) {
            flag = false;
        }

        //添加交易完毕后，需要创建交易记录
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setTranId(tran.getId());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setStage(tran.getStage());
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setCreateBy(tran.getCreateBy());
        tranHistory.setCreateTime(DateTimeUtil.getSysTime());
        int count3 = tranHistoryDao.save(tranHistory);
        if (count3 != 1) {
            flag = false;
        }

        return flag;
    }

    @Override
    public List<Tran> showTranList() {

        List<Tran> trans = tranDao.showTranList();
        return trans;
    }

    @Override
    public Tran detail(String id) {
        Tran tran = tranDao.detail(id);
        return tran;
    }

    @Override
    public List<TranHistory> getHistoryListByTranId(String tranId) {
        List<TranHistory> tranHistories = tranHistoryDao.getHistoryListByTranId(tranId);
        return tranHistories;
    }

    @Override
    public Boolean changeStage(Tran t) {
        boolean flag = true;

        //改变交易阶段
        int count1 = tranDao.changeStage(t);
        if (count1 != 1) {
            flag = false;
        }

        //添加交易历史
        TranHistory th = new TranHistory();

        String id = UUIDUtil.getUUID();
        String stage = t.getStage();
        String money = t.getMoney();
        String expectedDate = t.getExpectedDate();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = t.getEditBy();
        String tranId = t.getId();

        th.setStage(stage);
        th.setMoney(money);
        th.setExpectedDate(expectedDate);
        th.setCreateTime(createTime);
        th.setCreateBy(createBy);
        th.setTranId(tranId);
        th.setId(id);

        int count2 = tranHistoryDao.save(th);
        if (count2 != 1){
            flag = false;
        }

        return flag;
    }

    @Override
    public Map<String, Object> getCharts() {

        //取得total
        int total = tranDao.getTotal();

        List<Map<String,String>> dataList = tranDao.getCharts();


//        for (Map<String, String> map : dataList) {
//            Set<String> set = map.keySet();
//            for (String key : set){
//                System.out.println(key);
//                String s = map.get(key);
//                System.out.println(s);
//            }
//            System.out.println(map.get("name"));
//
//        }

//        for (Map<String, String> map : dataList) {
//            Set<String> set = map.keySet();
//            for (String key : set) {
//                System.out.println(key + "=" + map.get(key));
//            }
//            System.out.println();
//        }


        Map<String,Object> map = new HashMap<>();
        map.put("total",total);
        map.put("datalist",dataList);

        return map;
    }
}
