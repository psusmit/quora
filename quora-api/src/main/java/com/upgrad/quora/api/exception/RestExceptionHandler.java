package com.upgrad.quora.api.exception;

import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<com.upgrad.quora.api.model.ErrorResponse> signUpRestrictedException(SignUpRestrictedException exe, WebRequest request) {
        return new ResponseEntity<com.upgrad.quora.api.model.ErrorResponse>(
                new com.upgrad.quora.api.model.ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.CONFLICT
        );
    }
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<com.upgrad.quora.api.model.ErrorResponse> authenticationFailedException(AuthenticationFailedException exe, WebRequest request) {

        return new ResponseEntity<com.upgrad.quora.api.model.ErrorResponse>(
                new com.upgrad.quora.api.model.ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.UNAUTHORIZED
        );
    }
    @ExceptionHandler(SignOutRestrictedException.class)
    public ResponseEntity<com.upgrad.quora.api.model.ErrorResponse> signOutRestrictedException(SignOutRestrictedException exe, WebRequest request) {
        return new ResponseEntity<com.upgrad.quora.api.model.ErrorResponse>(
                new com.upgrad.quora.api.model.ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.UNAUTHORIZED
        );
    }


}
