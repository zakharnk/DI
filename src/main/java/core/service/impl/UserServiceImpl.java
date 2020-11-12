package core.service.impl;

import core.dao.EventDao;
import core.dao.UserDao;
import core.diContainer.annotations.Inject;
import core.service.UserService;

public class UserServiceImpl implements UserService {


    @Inject
    public UserServiceImpl(UserDao dao){

    }
    @Inject
    public UserServiceImpl(UserDao dao, EventDao eventDao){

    }


}
