package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userprofile")
public class CommonController {
    @Autowired
    UserBusinessService userBusinessService;

    @GetMapping(path = "/{userId}")
    public ResponseEntity<UserDetailsResponse> getUserById(@PathVariable("userId") String userId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserEntity userEntity = userBusinessService.getUserById(userId, authorization);
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse()
                .userName(userEntity.getUuid()).aboutMe(userEntity.getAboutMe())
                .contactNumber(userEntity.getContactNumber())
                .country(userEntity.getCountry()).dob(userEntity.getDob())
                .emailAddress(userEntity.getEmail()).userName(userEntity.getUserName())
                .firstName(userEntity.getFirstName()).lastName(userEntity.getLastName());
        return new ResponseEntity<>(userDetailsResponse, HttpStatus.OK);
    }
}
