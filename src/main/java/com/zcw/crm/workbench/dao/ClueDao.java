package com.zcw.crm.workbench.dao;

import com.zcw.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueDao {


    int save(Clue clue);

    Clue detail(String id);

    List<Clue> getClueList(Map<String, Object> map);

    int getClueTotal();

    Clue getById(String clueId);

    int delete(String clueId);
}
