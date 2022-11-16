package com.coronation.captr.login.controller;

import com.coronation.captr.login.interfaces.IResponse;
import com.coronation.captr.login.pojo.AuthRequest;
import com.coronation.captr.login.pojo.AuthResponse;
import com.coronation.captr.login.pojo.ChangePasswordReq;
import com.coronation.captr.login.pojo.Response;
import com.coronation.captr.login.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author toyewole
 */
@RestController
public class AuthController {

    @Autowired
    AuthService authService;


    @PostMapping
    @RequestMapping("sign-in")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody AuthRequest request) {

        AuthResponse authResponse = authService.handleUserLogin(request);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.AUTHORIZATION,
                authResponse.getToken());


        return new ResponseEntity<>(authResponse, responseHeaders, HttpStatus.OK);

    }

    @PostMapping
    @RequestMapping("change-password")
    public Response changePassword(@RequestBody ChangePasswordReq changePasswordReq) {
        return authService.handlePasswordChange(changePasswordReq);
    }

    @GetMapping
    @RequestMapping("forgot-password/{email}")
    public IResponse validateEmail(@PathVariable("email") String email) {

        return authService.handleForgotPasswordEmailValidation(email);

    }


    @GetMapping
    @RequestMapping("verify-email")
    public IResponse verifyEmail(@RequestParam("email") String email, @RequestParam("code") String code) {
        IResponse responseEnum = authService.confirmEmail(email, code);

        var resp = new Response();
        resp.setCode(responseEnum.getCode());
        resp.setDescription(responseEnum.getDescription());

        return resp;
    }

}
