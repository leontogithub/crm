package com.zcw.crm.workbench.service;

import com.zcw.crm.vo.PaginvationVo;
import com.zcw.crm.workbench.domain.Clue;
import com.zcw.crm.workbench.domain.Tran;

import java.util.Map;

public interface ClueService {

    boolean save(Clue clue);

    Clue detail(String id);

    boolean unbund(String id);

    boolean bund(String[] activityId,String clueId);


    PaginvationVo<Clue> getClueListVo(Map<String, Object> map);


    boolean convert(String clueId, String createBy, Tran tran);
}
