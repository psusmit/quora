package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserControllerBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserControllerBusinessService userControllerBusinessService;

    /* This method is for user registration in quora application.
     * It requests for all the attributes in 'SignupUserRequest'.
     *It throws SignUpRestrictedException - if the username or email already exist in the database.
     *It returns UUID of the user created.
     */
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/signup",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(SignupUserRequest signupUserRequest)
            throws SignUpRestrictedException {

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");
        userEntity.setContactNumber(signupUserRequest.getContactNumber());

        UserEntity createdUserEntity = userControllerBusinessService.signup(userEntity);
        SignupUserResponse userResponse =
                new SignupUserResponse()
                        .id(createdUserEntity.getUuid())
                        .status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    /*
     *This method is for a user to singin.
     *It returns SigninResponse which contains user id and a access-token in the response header.
     *It throws AuthenticationFailedException.
     */

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/signin",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(
            @RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException {

        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        UserAuthTokenEntity userAuthTokenEntity = userControllerBusinessService.signin(decodedArray[0], decodedArray[1]);

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthTokenEntity.getAccessToken());

        SigninResponse signinResponse = new SigninResponse();
        signinResponse.setId(userAuthTokenEntity.getUserEntity().getUuid());
        signinResponse.setMessage("SIGNED IN SUCCESSFULLY");

        return new ResponseEntity<>(signinResponse, headers, HttpStatus.OK);
    }

    /*
     *This method is used to sign out from the Quora Application.
     *AccessToken Token used for authenticating the user.
     *Returns UUID of the user who is signed out.
     *Throws SignOutRestrictedException.
     */
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/signout",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(
            @RequestHeader("authorization") final String accessToken) throws SignOutRestrictedException {
        UserEntity userEntity = userControllerBusinessService.signout(accessToken);
        SignoutResponse signoutResponse =
                new SignoutResponse().id(userEntity.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<>(signoutResponse, HttpStatus.OK);
    }

}

