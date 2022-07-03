package com.zcw.crm.workbench.dao;

import com.zcw.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranDao {

    int save(Tran tran);

    List<Tran> showTranList();

    Tran detail(String id);

    int changeStage(Tran t);

    int getTotal();

    List<Map<String, String>> getCharts();
}
