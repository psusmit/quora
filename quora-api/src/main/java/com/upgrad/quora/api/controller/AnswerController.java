package com.upgrad.quora.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
//@RequestMapping("/answer")
public class AnswerController {
    @Autowired
    AnswerBusinessService answerBusinessService;

    //@Autowired
    //QuestionBusinessService questionBusinessService;

    @GetMapping(path = "/answer/all/{questionId}")
    public ResponseEntity<String> getAllAnswersToQuestion(@PathVariable("questionId") String userId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

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



}
