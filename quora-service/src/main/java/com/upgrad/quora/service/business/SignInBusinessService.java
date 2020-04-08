package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Base64;

@Service

public class SignInBusinessService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity signIn(final String authorization) throws AuthenticationFailedException {
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        String username = decodedArray[0];
        String password = decodedArray[1];
        UserEntity userEntity = userDao.getUserByUserName(username);
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }
        userEntity = userDao.getUserByUserName(username);
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This user does noe exist");
        }
        final String encryptedPassword = passwordCryptographyProvider.encrypt(password, userEntity.getSalt());
        if (encryptedPassword.equals(userEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthTokenEntity = new UserAuthTokenEntity();
            UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();
            userAuthToken.setUser(userEntity);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthTokenEntity.setAccess_token(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));

            userAuthTokenEntity.setLogin_at(now);
            userAuthTokenEntity.setExpiresAt(expiresAt);
            userDao.createAuthToken(userAuthTokenEntity);

            userAuthTokenEntity.setExpiresAt(expiresAt);
            userAuthTokenEntity.setUuid(userEntity.getUuid());
            return userDao.createAuthToken(userAuthTokenEntity);

        } else {
            throw new AuthenticationFailedException("ATH-002", "Password Failed");
        }
    }
}