package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.GenericErrorCode;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UserBusinessService {

    @Autowired
    UserDao userDao;

    public UserEntity getUserById(String userId, String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {
        tokenValidation(authorizationToken);
        UserEntity user = userDao.getUserByUserId(userId);
        if (null == user)
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        else return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteUserById(String userId, String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = tokenValidation(authorizationToken);
        if ("nonadmin".equals(userAuthTokenEntity.getUser().getRole()))
            throw new AuthorizationFailedException(GenericErrorCode.ATHR_003.getCode(), GenericErrorCode.ATHR_003.getDefaultMessage());

        String userIdResponse = userDao.deleteUserByUserId(userId);
        if (null == userIdResponse)
            throw new UserNotFoundException(GenericErrorCode.USR_001.getCode(), GenericErrorCode.USR_001.getDefaultMessage());
        else return userIdResponse;
    }

    private UserAuthTokenEntity tokenValidation(String authorizationToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity == null)
            throw new AuthorizationFailedException(GenericErrorCode.ATHR_001.getCode(), GenericErrorCode.ATHR_001.getDefaultMessage());
        if (null != userAuthTokenEntity.getLogoutAt() && userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0)
            throw new AuthorizationFailedException(GenericErrorCode.ATHR_002.getCode(), GenericErrorCode.ATHR_002.getDefaultMessage());
        return userAuthTokenEntity;
    }
}
