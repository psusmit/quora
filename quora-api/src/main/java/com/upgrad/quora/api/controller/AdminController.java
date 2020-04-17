package com.upgrad.quora.api.controller;

import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    UserBusinessService userBusinessService;

    @DeleteMapping(path = "/user/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable("userId") String userId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        String userIdResponse = userBusinessService.deleteUserById(userId, authorization);
        return new ResponseEntity<>("USER SUCCESSFULLY DELETED", HttpStatus.OK);
    }
}
