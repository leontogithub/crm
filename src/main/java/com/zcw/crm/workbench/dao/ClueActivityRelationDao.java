package com.zcw.crm.workbench.dao;

import com.zcw.crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationDao {


    int unbund(String id);

    int bund(ClueActivityRelation car);

    List<ClueActivityRelation> getListByClueID(String clueId);

    int delete(ClueActivityRelation clueActivityRelation);
}
