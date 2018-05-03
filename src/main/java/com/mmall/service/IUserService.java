package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by Zhang Chen
 */
public interface IUserService {

    ServerResponse<User> login(String username, String password);
    ServerResponse<String> register(User user);
    ServerResponse<String> checkUserRegisterValidity(String str, String type);
    ServerResponse<String> getPasswordRecoveryQuestion(String username);
    ServerResponse<String> checkAnswerToRecoveryQuestion(String username, String question, String answer);
    ServerResponse<String> resetForgottenPassword(String username, String newPassword, String token);
    ServerResponse<String> resetPassword(String oldPassword, String newPassword, User user);
    ServerResponse<User> updateUserInfo(User user);
    ServerResponse<User> retrieveFullUserInfo(Integer userId);

    //backend-related services
    ServerResponse checkAdminRole(User user);
}
