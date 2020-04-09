package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserAuthTokenEntity getUserAuthToken(String authorizationToken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class).setParameter("accessToken", authorizationToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserEntity getUserByUserId(final String userId) {
        try {
            return entityManager.createNamedQuery("getUserByUserId", UserEntity.class).setParameter("userId", userId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public String deleteUserByUserId(String userId) {
        UserEntity user = getUserByUserId(userId);
        if (null != user) {
            entityManager.remove(user);
            return user.getUuid();
        }
        return null;
    }
}
