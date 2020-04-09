package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserAuthTokenDao {
    @PersistenceContext
    private EntityManager entityManager;

    public UserAuthTokenEntity getUserAuthByToken(final String accessToken) {
        try {
            return entityManager
                    .createNamedQuery("userAuthByAccessToken", UserAuthTokenEntity.class)
                    .setParameter("accessToken", accessToken)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    public void updateUserAuth(final UserAuthTokenEntity updatedUserAuthEntity) {
        entityManager.merge(updatedUserAuthEntity);
    }
}


