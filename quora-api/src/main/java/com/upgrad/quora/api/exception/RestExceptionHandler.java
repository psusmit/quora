package com.upgrad.quora.api.exception;


import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {


    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException exc, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(AuthorizationFailedException exc, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException exc, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse().code(exc.getCode()).message(exc.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestrictedException(SignUpRestrictedException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(InvalidQuestionException.class)
    public ResponseEntity<ErrorResponse> invalidQuestionException(InvalidQuestionException exe, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }


    @ExceptionHandler(SignOutRestrictedException.class)
    public ResponseEntity<com.upgrad.quora.api.model.ErrorResponse> signOutRestrictedException(SignOutRestrictedException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new com.upgrad.quora.api.model.ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AnswerNotFoundException.class)
    public ResponseEntity<com.upgrad.quora.api.model.ErrorResponse> answerNotFoundException(AnswerNotFoundException exe, WebRequest request) {
        return new ResponseEntity<com.upgrad.quora.api.model.ErrorResponse>(new com.upgrad.quora.api.model.ErrorResponse().code(exe.getCode()).
                message(exe.getErrorMessage()), HttpStatus.NOT_FOUND);
    }

}