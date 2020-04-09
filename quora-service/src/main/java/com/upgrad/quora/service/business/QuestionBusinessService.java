package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.GenericErrorCode;
import com.upgrad.quora.service.dao.QuestionDAO;
import com.upgrad.quora.service.dao.UserAuthTokenDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
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
public class QuestionBusinessService {
    @Autowired
    private QuestionDAO questionDAO;

    @Autowired
    private UserAuthTokenDao userAuthTokenDao;

    public QuestionEntity create(QuestionEntity questionEntity, String authorizationToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userAuthTokenDao.getUserAuthByToken(authorizationToken);
        if (userAuthTokenEntity == null)
            throw new AuthorizationFailedException(GenericErrorCode.ATHR_001.getCode(), GenericErrorCode.ATHR_001.getDefaultMessage());
        if (null != userAuthTokenEntity.getLogoutAt() && userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0)
            throw new AuthorizationFailedException(GenericErrorCode.ATHR_002.getCode(), "User is signed out.Sign in first to post a question");
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionDAO.create(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestions(String authorizationToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userAuthTokenDao.getUserAuthByToken(authorizationToken);
        if (userAuthTokenEntity == null)
            throw new AuthorizationFailedException(GenericErrorCode.ATHR_001.getCode(), GenericErrorCode.ATHR_001.getDefaultMessage());
        if (null != userAuthTokenEntity.getLogoutAt() && userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0)
            throw new AuthorizationFailedException(GenericErrorCode.ATHR_002.getCode(), "User is signed out.Sign in first to get all questions");
        return questionDAO.getAllQuestions();

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(QuestionEntity questionEntity, String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userAuthTokenDao.getUserAuthByToken(authorizationToken);
        if (userAuthTokenEntity == null)
            throw new AuthorizationFailedException(GenericErrorCode.ATHR_001.getCode(), GenericErrorCode.ATHR_001.getDefaultMessage());
        if (null != userAuthTokenEntity.getLogoutAt() && userAuthTokenEntity.getLogoutAt().compareTo(ZonedDateTime.now()) < 0)
            throw new AuthorizationFailedException(GenericErrorCode.ATHR_002.getCode(), "User is signed out.Sign in first to edit the question");

        QuestionEntity questionById = questionDAO.getQuestionById(questionEntity.getUuid());

        if (null == questionById)
            throw new InvalidQuestionException(GenericErrorCode.QUES_001.getCode(), GenericErrorCode.QUES_001.getDefaultMessage());
        if (!questionById.getUserEntity().getUuid().equals(userAuthTokenEntity.getUser().getUuid()))
            throw new AuthorizationFailedException(GenericErrorCode.ATHR_003.getCode(), "Only the question owner can edit the question");
        questionById.setContent(questionEntity.getContent());
        questionDAO.editQuestionContent(questionById);
        return questionById;
    }
}

