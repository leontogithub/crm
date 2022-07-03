package com.zcw.crm.workbench.service.impl;

import com.zcw.crm.utils.DateTimeUtil;
import com.zcw.crm.utils.SqlSessionUtil;
import com.zcw.crm.utils.UUIDUtil;
import com.zcw.crm.vo.PaginvationVo;
import com.zcw.crm.workbench.dao.*;
import com.zcw.crm.workbench.domain.*;
import com.zcw.crm.workbench.service.ClueService;

import java.util.List;
import java.util.Map;

public class ClueServiceImpl implements ClueService {
    //线索相关表
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);
    //客户相关表
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);
    //联系人相关表
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);
    //市场交易相关表
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);


    @Override
    public boolean save(Clue clue) {
        int count = clueDao.save(clue);
        return 1 == count;
    }

    @Override
    public Clue detail(String id) {
//        System.out.println("detail");
        Clue clue = clueDao.detail(id);
        System.out.println(clue);
        return clue;
    }

    @Override
    public boolean unbund(String id) {
        int count = clueActivityRelationDao.unbund(id);
        return 1 == count;
    }

    @Override
    public boolean bund(String[] activityId, String clueId) {

        boolean flag = true;

        for (String s : activityId) {
            ClueActivityRelation car = new ClueActivityRelation();
            car.setId(UUIDUtil.getUUID());
            System.out.println(car.getId());
            car.setClueId(clueId);
            car.setActivityId(s);

            int count = clueActivityRelationDao.bund(car);
            if (count != 1) {
                flag = false;
            }

        }

        return flag;
    }

    @Override
    public PaginvationVo<Clue> getClueListVo(Map<String, Object> map) {
        int total = clueDao.getClueTotal();
//        System.out.println(total);

        List<Clue> clues = clueDao.getClueList(map);

        PaginvationVo<Clue> pv = new PaginvationVo<>();
        pv.setTotal(total);
        pv.setDataList(clues);

        return pv;
    }

    @Override
    public boolean convert(String clueId, String createBy, Tran tran) {
        String createTime = DateTimeUtil.getSysTime();
        boolean flag = true;

        //通过线索id获取线索对象，
        Clue c = clueDao.getById(clueId);
        //通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司名称精确搜索，判断该客户是否存在）
        String company = c.getCompany();
        Customer cus = customerDao.getCustomerByName(company);
        System.out.println(cus);
        if (cus == null) {
            //客户不存在
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setAddress(c.getAddress());
            cus.setWebsite(c.getWebsite());
            cus.setPhone(c.getPhone());
            cus.setOwner(c.getOwner());
            cus.setNextContactTime(c.getNextContactTime());
            cus.setName(company);
            cus.setDescription(c.getDescription());
            cus.setCreateTime(createTime);
            cus.setCreateBy(createBy);
            cus.setContactSummary(c.getContactSummary());
            //添加客户
            int count = customerDao.save(cus);
            if (count != 1) {
                flag = false;
            }
        }
        System.out.println(cus.getId());

        //通过线索对象提取联系人信息，保存联系人
        Contacts con = new Contacts();
        con.setId(UUIDUtil.getUUID());
        con.setSource(c.getSource());
        con.setOwner(c.getOwner());
        con.setNextContactTime(c.getNextContactTime());
        con.setMphone(c.getMphone());
        con.setJob(c.getJob());
        con.setFullname(c.getFullname());
        con.setEmail(c.getEmail());
        con.setDescription(c.getDescription());

        //客户信息已拿到
        con.setCustomerId(cus.getId());
        con.setCreateTime(createTime);
        con.setCreateBy(createBy);
        con.setContactSummary(c.getContactSummary());
        con.setAppellation(c.getAppellation());
        con.setAddress(c.getAddress());

        //添加联系人
        int count = contactsDao.save(con);
        if (count != 1) {
            flag = false;
        }

        //线索备注转换到客户备注以及联系人备注
        //查询出该线索关联的备注信息列表

        List<ClueRemark> clueRemarkList = clueRemarkDao.getListByClueId(clueId);
        //取出每一条线索备注，主要转换备注信息
        for (ClueRemark clueRemark : clueRemarkList) {
            //取出备注信息
            String noteContent = clueRemark.getNoteContent();

            //创建客户备注对象
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setCustomerId(cus.getId());
            customerRemark.setEditFlag("0");  //未修改
            customerRemark.setNoteContent(noteContent);
            int count1 = customerRemarkDao.save(customerRemark);
            if (count1 != 1) {
                flag = false;
            }

            //创建联系人备注对象
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setContactsId(con.getId());
            contactsRemark.setEditFlag("0");  //未修改
            contactsRemark.setNoteContent(noteContent);
            int count2 = contactsRemarkDao.save(contactsRemark);
            if (count2 != 1) {
                flag = false;
            }

        }
        //线索和市场活动的关系转换到联系人和市场活动的关系
        //查询出与该条线索关联的市场活动,查询与市场活动的关联关系列表
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListByClueID(clueId);

        //遍历出每一条与市场活动关联的关联关系记录
        for (ClueActivityRelation clueActivityRelation : clueActivityRelationList) {
            //取出市场活动id
            String activityId = clueActivityRelation.getActivityId();

            //创建联系人与市场活动的关联关系的对象，上面生成的contact
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            contactsActivityRelation.setContactsId(con.getId());
            contactsActivityRelation.setActivityId(activityId);

            //添加关联关系
            int count3 = contactsActivityRelationDao.save(contactsActivityRelation);
            if (count3 != 1) {
                flag = false;
            }

        }


        //如果有交易需求，创建一条交易
        if (tran != null) {
            //tran在controller已经封装
            //可以通过第一步生成的c对象（线索) 取出信息继续完善tran对象封装
            tran.setSource(c.getSource());
            tran.setOwner(c.getOwner());
            tran.setNextContactTime(c.getNextContactTime());
            tran.setDescription(c.getDescription());
            tran.setCustomerId(cus.getId());
            tran.setContactsId(con.getId());
            //添加交易
            int count4 = tranDao.save(tran);
            if (count4 != 1) {
                flag = false;
            }

            //如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setCreateBy(createBy);
            tranHistory.setCreateTime(createTime);
            tranHistory.setExpectedDate(tran.getExpectedDate());
            tranHistory.setMoney(tran.getMoney());
            tranHistory.setStage(tran.getStage());
            tranHistory.setTranId(tran.getId());

            //添加交易历史
            int count5 = tranHistoryDao.save(tranHistory);
            if (count5 != 1) {
                flag = false;
            }
        }

        //删除线索备注
        for (ClueRemark clueRemark : clueRemarkList) {
            int count6 = clueRemarkDao.delete(clueRemark);

            if (count6 != 1) {
                flag = false;
            }
        }

        //删除线索和市场活动的关系
        for (ClueActivityRelation clueActivityRelation : clueActivityRelationList) {
            int count7 = clueActivityRelationDao.delete(clueActivityRelation);
            if (count7 !=1){
                flag = false;
            }
        }

        //删除线索
        int count8 = clueDao.delete(clueId);
        if (count8 != 1){
            flag = false;
        }

        return flag;
    }


}
