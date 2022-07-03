package com.zcw.crm.settings.dao;

import com.zcw.crm.settings.domain.DicValue;

import java.util.List;

public interface DicValueDao {
    List<DicValue> getTypeValueList(String code);
}
