package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDAO {
    @PersistenceContext
    private EntityManager entityManager;

    public void create(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
    }

    public List<QuestionEntity> getAllQuestions() {
        return entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
    }

    public QuestionEntity getQuestionById(String questionId) {
        try {
            return entityManager.createNamedQuery("getQuestionByUUID", QuestionEntity.class)
                    .setParameter("questionId", questionId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    public QuestionEntity getQuestionByUId(final String questionId) {
        try {
            return entityManager
                    .createNamedQuery("getQuestionByUId", QuestionEntity.class)
                    .setParameter("uuid", questionId)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void deleteQuestion(QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }

    public List<QuestionEntity> getAllQuestionsByUser(final UserEntity userId) {
        return entityManager
                .createNamedQuery("getQuestionByUser", QuestionEntity.class)
                .setParameter("user", userId)
                .getResultList();
    }

    public void editQuestionContent(QuestionEntity question) {
        entityManager.merge(question);
    }
}
