package com.upgrad.quora.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
//@RequestMapping("/answer")
public class AnswerController {
    @Autowired
    AnswerBusinessService answerBusinessService;

    //@Autowired
    //QuestionBusinessService questionBusinessService;

    @GetMapping(path = "/answer/all/{questionId}")
    public ResponseEntity<String> getAllAnswersToQuestion(@PathVariable("questionId") String userId,
                                                          @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {

        List<AnswerEntity> list = answerBusinessService.getAllAnswersToQuestionById(userId, authorization);
        List<Map<String, String>> values = new ArrayList<>();//= list.stream().map(e -> e.forEach(key, value)->))

        for (AnswerEntity ans : list) {
            Map<String, String> map = new HashMap<>();
            map.put("uuid", ans.getUuid());
            map.put("ans", ans.getAnswer());
            map.put("ques", ans.getQuestionEntity().getContent());
            values.add(map);
        }

        List<JSONObject> json_list = new ArrayList<>();
        for (Map<String, String> map : values) {
            JSONObject jsonObject = new JSONObject(map);
            json_list.add(jsonObject);
        }
        JSONArray jsonArray = new JSONArray(json_list);
        return new ResponseEntity<>(jsonArray.toString(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswerForQuestion(@PathVariable("questionId") String questionId,
                                            AnswerRequest answerRequest, @RequestHeader("authorization") final String authorization)
                                                throws AuthorizationFailedException, InvalidQuestionException {

        AnswerEntity answerEntity = answerBusinessService.createAnswerForQuestion(questionId, answerRequest.getAnswer(), authorization);

        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> updateAnswer(@PathVariable("answerId") String answerId,
                                               AnswerRequest answerRequest, @RequestHeader("authorization") final String authorization)
                                                    throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity answerEntity = answerBusinessService.updateAnswer(answerId, answerRequest.getAnswer(), authorization);

        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> deleteAnswer(@PathVariable("answerId") String answerId,
                                                        @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity answerEntity = answerBusinessService.deleteAnswer(answerId, authorization);

        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.OK);
    }
}
