package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/")
public class AnswerController {
    @Autowired
    AnswerBusinessService answerBusinessService;

    /*
     *This method is used to get all answers to a particular question.
     * Requests the path variable 'questionId'  and access token of the signed in user.
     * Throws AuthorizationFailedException, InvalidQuestionException.
     * return "uuid" of the answer, "content" of the question and "content" of all the answers posted for that particular question
     */
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

  /*
  *This endpoint is used to edit an answer.
  * Requests for all the attributes in "AnswerEditRequest", the path variable 'answerId' and access token of the signed in user.
  * Throws AuthorizationFailedException, AnswerNotFoundException.
  * Return "uuid" of the edited answer.
   */
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> updateAnswer(@PathVariable("answerId") String answerId,
                                                       AnswerRequest answerRequest, @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity answerEntity = answerBusinessService.updateAnswer(answerId, answerRequest.getAnswer(), authorization);

        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<>(answerResponse, HttpStatus.OK);
    }
    /*
     *This method is used to delete an answer.
     * Requests for the path variable 'answerId' and access token of the signed in.
     * Throws AuthorizationFailedException, AnswerNotFoundException.
     * Return "uuid" of the deleted answer
     */

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> deleteAnswer(@PathVariable("answerId") String answerId,
                                                       @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity answerEntity = answerBusinessService.deleteAnswer(answerId, authorization);

        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER DELETED");
        return new ResponseEntity<>(answerResponse, HttpStatus.OK);
    }

    /*
     *This method is to create an answer to a particular question.
     * Requests for the attribute in "Answer Request", the path variable 'questionId ' and access token of the signed in user.
     *Throws AuthorizationFailedException, InvalidQuestionException.
     *  Return the "uuid" of the answer.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswerForQuestion(@PathVariable("questionId") String questionId,
                                                                  AnswerRequest answerRequest, @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {

        AnswerEntity answerEntity = answerBusinessService.createAnswerForQuestion(questionId, answerRequest.getAnswer(), authorization);

        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<>(answerResponse, HttpStatus.OK);
    }

}
