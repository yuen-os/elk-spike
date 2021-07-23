package com.elk.spike.elkspike.service.user;

import com.elk.spike.elkspike.annotation.ProcessListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.List;


@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepo userRepo;

    @Override
    public List<User> findAllUser() {
        return userRepo.findAll();
    }
}
