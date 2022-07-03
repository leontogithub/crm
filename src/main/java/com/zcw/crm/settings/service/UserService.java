package com.zcw.crm.settings.service;

import com.zcw.crm.exception.LoginException;
import com.zcw.crm.settings.domain.User;

import java.util.List;

public interface UserService {
    User login(String loginAct, String loginPwd, String ip) throws LoginException;

    List<User> getUserLiset();

}
