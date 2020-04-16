package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.GenericErrorCode;
import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDAO;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnswerBusinessService {

    @Autowired
    UserDao userDao;

    @Autowired
    AnswerDao answerDao;

    @Autowired
    QuestionDAO questionDAO;

    public List<AnswerEntity> getAllAnswersToQuestionById(String questionId, String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        validateAuthorizationToken(authorizationToken);
        List<AnswerEntity> answers = answerDao.getAllAnswersForQuestion(questionId);
        if (null == answers || answers.size() == 0)
            throw new InvalidQuestionException(GenericErrorCode.QUES_001.getCode(), GenericErrorCode.QUES_001.getDefaultMessage());
        else return answers;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswerForQuestion(String questionId, String answer, String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthTokenEntity = validateAuthorizationToken(authorizationToken);

        QuestionEntity questionEntity = questionDAO.getQuestionById(questionId);
        if (questionEntity == null)
            throw new InvalidQuestionException(GenericErrorCode.QUES_001.getCode(), GenericErrorCode.QUES_001.getDefaultMessage());

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setAnswer(answer);
        answerEntity.setQuestionEntity(questionEntity);
        answerEntity.setUserEntity(userAuthTokenEntity.getUserEntity());
        answerEntity = answerDao.createAnswer(answerEntity);
        return answerEntity;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity updateAnswer(String answerId, String answer, String authorizationToken) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity == null)
            throw new AuthorizationFailedException(GenericErrorCode.ATHR_001.getCode(), GenericErrorCode.ATHR_001.getDefaultMessage());
        if (null != userAuthTokenEntity.getLogoutAt() && userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0)
            throw new AuthorizationFailedException(GenericErrorCode.ATHR_002.getCode(), "User is signed out.Sign in first to edit an answer");

        AnswerEntity answerEntity = answerDao.getAnswerByUUID(answerId);
        if (answerEntity == null)
            throw new AnswerNotFoundException(GenericErrorCode.ANS_001.getCode(), GenericErrorCode.ANS_001.getDefaultMessage());
        if (!userAuthTokenEntity.getUser().getUuid().equals(answerEntity.getUserEntity().getUuid()))
            throw new AuthorizationFailedException(GenericErrorCode.ATHR_003.getCode(), "Only the answer owner can edit the answer");
        answerEntity.setAnswer(answer);
        answerEntity = answerDao.saveOrUpdateAnswer(answerEntity);

        return answerEntity;

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(String answerId, String authorizationToken) throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity = validateAuthorizationToken(authorizationToken);
        AnswerEntity answerEntity = answerDao.getAnswerByUUID(answerId);

        if (answerEntity == null)
            throw new AnswerNotFoundException(GenericErrorCode.ANS_001.getCode(), GenericErrorCode.ANS_001.getDefaultMessage());
        if (!userAuthTokenEntity.getUser().getRole().equals("admin") && !userAuthTokenEntity.getUser().getUuid().equals(answerEntity.getUserEntity().getUuid()))
            throw new AuthorizationFailedException(GenericErrorCode.ATHR_003.getCode(), "Only the answer owner or admin can delete the answer");
        answerDao.deleteAnswer(answerEntity);
        return answerEntity;

    }

    private UserAuthTokenEntity validateAuthorizationToken(String authorizationToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity == null)
            throw new AuthorizationFailedException(GenericErrorCode.ATHR_001.getCode(), GenericErrorCode.ATHR_001.getDefaultMessage());
        if (null != userAuthTokenEntity.getLogoutAt() && userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0)
            throw new AuthorizationFailedException(GenericErrorCode.ATHR_002.getCode(), "User is signed out.Sign in first to delete an answer");
        return userAuthTokenEntity;
    }

}
