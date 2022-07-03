package com.zcw.crm.workbench.service.impl;

import com.zcw.crm.settings.dao.UserDao;
import com.zcw.crm.settings.domain.User;
import com.zcw.crm.utils.SqlSessionUtil;
import com.zcw.crm.vo.PaginvationVo;
import com.zcw.crm.workbench.dao.ActivityDao;
import com.zcw.crm.workbench.dao.ActivityRemarkDao;
import com.zcw.crm.workbench.domain.Activity;
import com.zcw.crm.workbench.domain.ActivityRemark;
import com.zcw.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
    private final ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override
    public boolean save(Activity a) {
        boolean falg = true;
        int nums = activityDao.save(a);
        if (nums != 1) {
            falg = false;
        }
        return falg;
    }

    @Override
    public PaginvationVo<Activity> pageList(Map<String, Object> map) {
        //取得total,
        int total = activityDao.getTotalByCondition(map);
        //取得datalist
        List<Activity> activityList = activityDao.getActivityListByCondition(map);
        //将datalist和total封装到vo
        PaginvationVo<Activity> vo = new PaginvationVo<>();
        vo.setDataList(activityList);
        vo.setTotal(total);

        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        boolean flag = true;
        //需要删除的市场活动备注数量，备注表
        int count1 = activityRemarkDao.getCountByIds(ids);
        //实际备注后返回的受影响记录条数
        int count2 = activityRemarkDao.deleteByIds(ids);

        //判断是否完全删除成功
        if (count1 != count2) {
            flag = false;
        }
        //删除市场活动
        int count = activityDao.delete(ids);
        if (count != ids.length) {
            flag = false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        //取userlist
        List<User> userList = userDao.getUserList();
        //取activity
        Activity activity = activityDao.getById(id);
        //封装成map返回
        Map<String, Object> map = new HashMap<>();
        map.put("userList", userList);
        map.put("a", activity);

        return map;
    }

    @Override
    public boolean update(Activity a) {
        int count = activityDao.update(a);

        return 1 == count;
    }

    @Override
    public Activity detail(String id) {
        Activity a = activityDao.detail(id);

        return a;
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String activityId) {
        List<ActivityRemark> remarks = activityRemarkDao.getRemarkListByAid(activityId);
        return remarks;
    }

    @Override
    public boolean deleteRemark(String id) {
        int count = activityRemarkDao.deleteRemark(id);
        System.out.println(count);
        return 1 == count;
    }

    @Override
    public boolean saveRemark(ActivityRemark ar) {

        int count = activityRemarkDao.saveRemark(ar);

        return 1 == count;
    }

    @Override
    public boolean updateRemark(ActivityRemark ar) {
        int count = activityRemarkDao.updateRemark(ar);
        return 1 == count;
    }

    @Override
    public List<Activity> getActivityListByClueId(String clueId) {
        List<Activity> activities = activityDao.getActivityListByClueId(clueId);
        return activities;
    }

    @Override
    public List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map) {
        List<Activity> activities = activityDao.getActivityListByNameAndNotByClueId(map);
        return activities;
    }

    @Override
    public List<Activity> getActivityListByName(String name) {
        List<Activity> activities = activityDao.getActivityListByName(name);
        return activities;
    }

}
