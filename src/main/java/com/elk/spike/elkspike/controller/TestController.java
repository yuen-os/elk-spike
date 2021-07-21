package com.elk.spike.elkspike.controller;

import com.elk.spike.elkspike.annotation.ProcessListener;
import com.elk.spike.elkspike.service.user.UserService;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@ProcessListener
@RestController
public class TestController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @GetMapping("/users/{userId}")
    public Object getUserById(@PathVariable(value = "userId") long userId,
                      @RequestParam(value = "fetch", required = false) String fetch){

        if(userId<1){
            //create an error
            int a = 1/0;
        }
//        logger.info();
        return ResponseEntity.status(200).body(Map.of("message","ok") );
    }

    @GetMapping("/users")
    public Object getUsers(){
        return ResponseEntity.status(200).body(Map.of("message",userService.findAllUser()) );
    }
}
