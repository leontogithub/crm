package com.zcw.crm.workbench.dao;

import com.zcw.crm.workbench.domain.Contacts;

import java.util.List;

public interface ContactsDao {


   int save(Contacts con);

    List<Contacts> searchContactsByFullname(String funllname);
}
