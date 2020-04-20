package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;

    public List<AnswerEntity> getAllAnswersForQuestion(final String questionId) {
        try {
            return entityManager
                    .createNamedQuery("answersByQuestionUUID", AnswerEntity.class)
                    .setParameter("questionId", questionId)
                    .getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public AnswerEntity createAnswer(final AnswerEntity answerEntity) {
        try {
            entityManager.persist(answerEntity);
            return answerEntity;
        } catch (NoResultException nre) {
            return null;
        }


    }

    public AnswerEntity getAnswerByUUID(final String answerId) {
        try {
            return entityManager
                    .createNamedQuery("getAnswerByUUID", AnswerEntity.class)
                    .setParameter("answerId", answerId)
                    .getSingleResult();

        } catch (NoResultException nre) {
            return null;
        }
    }

    public AnswerEntity saveOrUpdateAnswer(final AnswerEntity answerEntity) {
        try {
            return entityManager.merge(answerEntity);

        } catch (NoResultException nre) {
            return null;
        }
    }

    public AnswerEntity deleteAnswer(final AnswerEntity answerEntity) {
        try {
            entityManager.remove(answerEntity);

            return answerEntity;

        } catch (NoResultException nre) {
            return null;
        }
    }
}