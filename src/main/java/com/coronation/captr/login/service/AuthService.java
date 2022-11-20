package com.coronation.captr.login.service;

import com.coronation.captr.login.entities.CTPrivilege;
import com.coronation.captr.login.entities.MessageTrail;
import com.coronation.captr.login.entities.User;
import com.coronation.captr.login.enums.IResponseEnum;
import com.coronation.captr.login.interfaces.IResponse;
import com.coronation.captr.login.pojo.ActivityLog;
import com.coronation.captr.login.pojo.AuthRequest;
import com.coronation.captr.login.pojo.AuthResponse;
import com.coronation.captr.login.pojo.ChangePasswordReq;
import com.coronation.captr.login.pojo.MessagePojo;
import com.coronation.captr.login.pojo.Response;
import com.coronation.captr.login.respositories.IMessageTrailsRespository;
import com.coronation.captr.login.respositories.IUserRespository;
import com.coronation.captr.login.util.AppProperties;
import com.coronation.captr.login.util.Constants;
import com.coronation.captr.login.util.JsonWebTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author toyewole
 */
@Service
@Slf4j
public class AuthService {


    @Autowired
    IUserRespository iUserRespository;

    @Autowired
    IMessageTrailsRespository IMessageTrailsRespository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JsonWebTokenUtil jsonWebTokenUtil;

    @Autowired
    AppProperties appProperties;

    @Autowired
    RabbitTemplate rabbitTemplate;


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
                            authResponse.setPrivileges(user.getPrivilegeList().stream().map(CTPrivilege::getCode).collect(Collectors.toList()));

                            sendActivityLog("LOGGED_IN", user.getEmail(), authResponse.getDescription());

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

        Optional<MessageTrail> otpOptional = IMessageTrailsRespository.findTopByRecipientOrderByCreateDateDesc(email)
                .filter(trail -> StringUtils.equals(code, trail.getCode()));

        if (otpOptional.isPresent()) {

            IResponse response = iUserRespository.findByEmail(email)
                    .map(user -> IResponseEnum.SUCCESS)
                    .orElse(IResponseEnum.INVALID_TOKEN);

            sendActivityLog("EMAIL_CONFIRMATION", email, response.getDescription());

            return response;

        }


        return IResponseEnum.INVALID_TOKEN;
    }

    public IResponse handleForgotPasswordEmailValidation(String email) {
        Response response = new Response();
        response.setCode(-1);

        if (StringUtils.isBlank(email)) {
            response.setDescription("Kindly provide username/email");
            return response;
        }

        iUserRespository.findByEmail(email)
                .ifPresentOrElse(user -> {
                            sendEmailConfirmation(user, UUID.randomUUID().toString());

                            response.setDescription("Change of password link has been sent to your email");
                            response.setCode(IResponseEnum.SUCCESS.getCode());
                        },
                        () -> {
                            response.setDescription("User is not found!!. Kindly confirm your email address");
                            response.setCode(IResponseEnum.ERROR.getCode());

                        });


        return response;


    }

    @Transactional
    public Response handlePasswordChange(ChangePasswordReq changePasswordReq) {

        final Response resp = isReqValid(changePasswordReq);

        if (IResponseEnum.SUCCESS.getCode() != resp.getCode()) {
            return resp;
        }
        resp.setCode(IResponseEnum.ERROR.getCode());
        resp.setDescription("Request failed validation. Kindly contact support! ");

        return IMessageTrailsRespository.findTopByRecipientOrderByCreateDateDesc(changePasswordReq.getEmail())
                .filter(trail -> StringUtils.equals(changePasswordReq.getToken(), trail.getCode()))
                .map(otpTrails -> {
                    iUserRespository.updatePasswordAndLastModified(
                            passwordEncoder.encode(changePasswordReq.getConfirmPassword()), changePasswordReq.getEmail());
                    resp.setCode(IResponseEnum.SUCCESS.getCode());
                    resp.setDescription("Password changed successfully!!");

                    sendActivityLog("PASSWORD_CHANGE", changePasswordReq.getEmail(), resp.getDescription());
                    return resp;
                }).orElse(resp);


    }

    private Response isReqValid(ChangePasswordReq changePasswordReq) {
        Response resp = new Response();
        resp.setCode(IResponseEnum.ERROR.getCode());

        if (StringUtils.isBlank(changePasswordReq.getNewPassword())) {
            resp.setDescription("Password cannot be blank");
            return resp;
        }

        if (!StringUtils.equals(changePasswordReq.getNewPassword(), changePasswordReq.getConfirmPassword())) {
            resp.setDescription("Password must be the same with Confirm Password");
            return resp;
        }

        if (StringUtils.isBlank(changePasswordReq.getEmail()) || StringUtils.isBlank(changePasswordReq.getToken())) {
            resp.setDescription("Request failed authentication. Kindly contact support.");
            return resp;
        }

        resp.setCode(IResponseEnum.SUCCESS.getCode());
        return resp;

    }

    @Async
    public void sendEmailConfirmation(User user, String token) {
        MessagePojo message = new MessagePojo();


        var expTime = LocalDateTime.now().plusMinutes(appProperties.getEmailConfirmationExpTime());
        String link = String.format(appProperties.getEmailConfirmationLink(), user.getEmail(), token);
        message.setMessageBody(String.format(appProperties.getChangeOfPasswordMessage(), user.getFirstName(), link, expTime.format(Constants.DATE_TIME_FORMATTER)));
        message.setRecipient(user.getEmail());
        message.setSource("user-service");
        message.setSubject("Email Confirmation");
        message.setCode(token);

        message.setRequestTime(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER));

        try {
            rabbitTemplate.convertAndSend(appProperties.getNotificationExchange(), appProperties.getRoutingKey(), message);
        } catch (Exception e) {
            log.debug("Error occurred while pushing email confirmation", e);
        }
    }

    @Async
    public void sendActivityLog(String activityType, String email, String description) {

        ActivityLog activityLog = new ActivityLog();
        activityLog.setActivityType(activityType);
        activityLog.setDescription(description);
        activityLog.setRequestTime(LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER));
        activityLog.setEmailAddress(email);
        try {
            rabbitTemplate.convertAndSend(appProperties.getActivityExchange(), appProperties.getActivityLogRoutingKey(), activityLog);
            log.debug("Activity Logged successfully");
        } catch (Exception e) {
            log.debug("Error occurred while logging activity", e);
        }
    }

}
