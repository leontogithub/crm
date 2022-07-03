package com.zcw.crm.workbench.service;

import com.zcw.crm.workbench.domain.Contacts;
import com.zcw.crm.workbench.domain.Tran;
import com.zcw.crm.workbench.domain.TranHistory;

import java.util.List;
import java.util.Map;

public interface TranService {
    List<Contacts> searchContactsByFullname(String funllname);

    boolean save(Tran tran, String customerName);

    List<Tran> showTranList();

    Tran detail(String id);

    List<TranHistory> getHistoryListByTranId(String tranId);

    Boolean changeStage(Tran t);

    Map<String, Object> getCharts();
}
