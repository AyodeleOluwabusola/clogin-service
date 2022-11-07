package com.coronation.captr.login.service;

import com.coronation.captr.login.entities.User;
import com.coronation.captr.login.enums.IResponseEnum;
import com.coronation.captr.login.interfaces.IResponse;
import com.coronation.captr.login.pojo.AuthRequest;
import com.coronation.captr.login.pojo.AuthResponse;
import com.coronation.captr.login.respositories.IUserRespository;
import com.coronation.captr.login.util.JsonWebTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author toyewole
 */
@Service
@Slf4j
public class AuthService {


    @Autowired
    IUserRespository iUserRespository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JsonWebTokenUtil jsonWebTokenUtil;


    public AuthResponse handleUserLogin(AuthRequest authRequest) {
        var authResponse = validateRequest(authRequest);

        if (IResponseEnum.SUCCESS.getCode() != authResponse.getCode()) {
            return authResponse;
        }

        iUserRespository.findByEmail(authRequest.getUsername())
                .filter(user -> passwordEncoder.matches(authRequest.getPassword(), user.getPassword()))
                .ifPresentOrElse(user -> {

                            if (user.getEmailConfirmationTimestamp() != null) {
                                log.debug(" user email {} not yet confirmed", user.getEmail());
                                authResponse.setCode(IResponseEnum.EMAIL_CONFIRMATION_ERROR.getCode());
                                authResponse.setDescription("Kindly confirm you're email before you can login.");
                                return;
                            }

                            authResponse.setCode(IResponseEnum.SUCCESS.getCode());
                            authResponse.setDescription("Login successful");
                            authResponse.setToken(handleTokenGeneration(user));

                        },
                        () -> {
                            authResponse.setCode(IResponseEnum.ERROR.getCode());
                            authResponse.setDescription("Email Address or password is not correct ");
                        });

        return authResponse;

    }

    private String handleTokenGeneration(User user) {
        return jsonWebTokenUtil.generateToken(user.getEmail());
    }

    private AuthResponse validateRequest(AuthRequest request) {
        AuthResponse response = new AuthResponse();
        if (StringUtils.isBlank(request.getUsername())) {
            response.setDescription("Kindly provide a email address ");
            return response;

        }
        if (StringUtils.isBlank(request.getPassword())) {
            response.setDescription("Kindly provide a password ");
            return response;
        }

        return response;
    }

    public IResponse confirmEmail(String email, String code) {

        if (StringUtils.isBlank(email) || StringUtils.isBlank(code)) {
            return IResponseEnum.INVALID_VALIDATION_PARAM;
        }

        return iUserRespository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(code, user.getConfirmationCode()))
                .map(user -> IResponseEnum.SUCCESS)
                .orElse(IResponseEnum.INVALID_TOKEN);


    }
}
