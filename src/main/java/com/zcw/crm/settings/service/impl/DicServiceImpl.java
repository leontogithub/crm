package com.zcw.crm.settings.service.impl;

import com.zcw.crm.settings.dao.DicTypeDao;
import com.zcw.crm.settings.dao.DicValueDao;
import com.zcw.crm.settings.domain.DicType;
import com.zcw.crm.settings.domain.DicValue;
import com.zcw.crm.settings.service.DicService;
import com.zcw.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {
    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    public DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);

    @Override
    public Map<String, Object> getAll() {

        Map<String,Object> map = new HashMap<>();

        //将字典类型列表取出
        List<DicType> typeList = dicTypeDao.getTypeList();
        for (DicType dicType : typeList) {
            String code = dicType.getCode();
            //根据每一个字典列表取得字典值列表
            List<DicValue> dicValueList = dicValueDao.getTypeValueList(code);
            map.put(code,dicValueList);

        }

        return map;
    }
}
