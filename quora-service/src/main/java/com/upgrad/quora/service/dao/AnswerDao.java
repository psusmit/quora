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
                    .createNamedQuery("answersByQuestionId", AnswerEntity.class)
                    .setParameter("questionId", questionId)
                    .getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
