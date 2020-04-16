package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class QuestionDAO {

    @PersistenceContext
    private EntityManager entityManager;


    public QuestionEntity getQuestionById(String questionId) {
        try {
            return entityManager
                    .createNamedQuery("getQuestionByUUID", QuestionEntity.class)
                    .setParameter("questionId", questionId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}