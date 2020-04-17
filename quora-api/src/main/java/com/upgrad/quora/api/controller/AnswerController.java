package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.api.model.AnswerDetailsResponse;

import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
public class AnswerController {
    @Autowired
    AnswerBusinessService answerBusinessService;

    @GetMapping(path = "/answer/all/{questionId}")
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable("questionId") String userId,
                                                          @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {

        List<AnswerEntity> answersList = answerBusinessService.getAllAnswersToQuestionById(userId, authorization);

        List<AnswerDetailsResponse> answersResponseList = answersList.parallelStream().map(ans ->{
                AnswerDetailsResponse ansRes = new AnswerDetailsResponse();
                ansRes.setId(ans.getUuid());
                ansRes.setAnswerContent(ans.getAnswer());
                ansRes.setQuestionContent(ans.getQuestionEntity().getContent());
                return ansRes;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(answersResponseList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswerForQuestion(@PathVariable("questionId") String questionId,
                                            AnswerRequest answerRequest, @RequestHeader("authorization") final String authorization)
                                                throws AuthorizationFailedException, InvalidQuestionException {

        AnswerEntity answerEntity = answerBusinessService.createAnswerForQuestion(questionId, answerRequest.getAnswer(), authorization);

        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<>(answerResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> updateAnswer(@PathVariable("answerId") String answerId,
                                               AnswerRequest answerRequest, @RequestHeader("authorization") final String authorization)
                                                    throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity answerEntity = answerBusinessService.updateAnswer(answerId, answerRequest.getAnswer(), authorization);

        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<>(answerResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> deleteAnswer(@PathVariable("answerId") String answerId,
                                                        @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity answerEntity = answerBusinessService.deleteAnswer(answerId, authorization);

        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");
        return new ResponseEntity<>(answerResponse, HttpStatus.OK);
    }
}
