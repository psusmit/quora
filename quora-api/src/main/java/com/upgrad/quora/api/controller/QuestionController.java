package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    private QuestionBusinessService questionBusinessService;

    /*
     * This endpoint is used to create a question in the Quora Application.
     *Requests for all the attributes in 'QuestionRequest'access token of the signed in user.
     *Throws AuthorizationFailedException
     *Returns UUID of the question created in DB.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> create(final QuestionRequest questionRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        QuestionEntity createdQuestionEntity = questionBusinessService.create(questionEntity, authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestionEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<>(questionResponse, HttpStatus.CREATED);
    }

    /*
     *This method is used to fetch all the questions that have been posted in the application by any user.
     *  Requests for access token of the signed in user.
     * Throws AuthorizationFailedException.
     * Return List of QuestionDetailsResponse.
     */
    @GetMapping(path = "/all")
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        List<QuestionEntity> questionList = questionBusinessService.getAllQuestions(authorization);
        List<QuestionDetailsResponse> questionResponseList = new ArrayList<>(questionList.size());
        questionList.stream().parallel().forEach(question -> {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
            questionDetailsResponse.setId(question.getUuid());
            questionDetailsResponse.setContent(question.getContent());
            questionResponseList.add(questionDetailsResponse);
        });
        return new ResponseEntity<>(questionResponseList, HttpStatus.OK);
    }
    /*
     *This endpoint is used to edit a question that has been posted by a user.
     * Requests for all the attributes in 'QuestionEditRequest', the path variable 'questionId', access token of the signed in user.
     * Throws AuthorizationFailedException,InvalidQuestionException.
     */

    @PutMapping(path = "edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(@PathVariable("questionId") String questionId, final QuestionEditRequest questionEditRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(questionId);
        questionEntity.setContent(questionEditRequest.getContent());
        QuestionEntity questionContent = questionBusinessService.editQuestionContent(questionEntity, authorization);

        return new ResponseEntity<>(new QuestionEditResponse().id(questionContent.getUuid()).status("QUESTION EDITED"), HttpStatus.OK);
    }

    /*
     *This endpoint is used to delete a question that has been posted by a user.
     *Requests for the path variable 'questionId'and  and access token of the signed in user.
     * Throws AuthorizationFailedException,InvalidQuestionException.
     * Return 'uuid' of the deleted question.
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/delete/{questionId}")
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
            @RequestHeader("authorization") final String accessToken,
            @PathVariable("questionId") final String questionId)
            throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity questionEntity = questionBusinessService.deleteQuestion(accessToken, questionId);
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse();
        questionDeleteResponse.setId(questionEntity.getUuid());
        questionDeleteResponse.setStatus("QUESTION DELETED");
        return new ResponseEntity<>(questionDeleteResponse, HttpStatus.OK);
    }

    /*
     *This endpoint is used to fetch all the questions posed by a specific user.
     *  Requests the path variable 'userId' and access token of the signed in user.
     * Throws  AuthorizationFailedException, UserNotFoundException.
     * Returns  return 'uuid' and 'content' of all the questions posted by the corresponding user.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/all/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getQuestionByUserId(
            @RequestHeader("authorization") final String accessToken,
            @PathVariable("userId") String userId)
            throws AuthorizationFailedException, UserNotFoundException {

        List<QuestionEntity> questions = questionBusinessService.getAllQuestionsByUser(userId, accessToken);
        List<QuestionDetailsResponse> questionDetailResponses = new ArrayList<>();
        for (QuestionEntity questionEntity : questions) {
            QuestionDetailsResponse questionDetailResponse = new QuestionDetailsResponse();
            questionDetailResponse.setId(questionEntity.getUuid());
            questionDetailResponse.setContent(questionEntity.getContent());
            questionDetailResponses.add(questionDetailResponse);
        }
        return new ResponseEntity<>(questionDetailResponses, HttpStatus.OK);
    }


}
