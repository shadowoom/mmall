package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Zhang Chen
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * Controller for user login
     * @param username
     * @param password
     * @param session
     * @HttpRequest POST
     * @return ServerResponse<User>
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        //return response;
        return response;
    }

    /**
     * Controller for user logout
     * @param session
     * @HttpRequest POST
     * @return ServerResponse<String>
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    // user register API
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    // user register validation API
    @RequestMapping(value = "check_user_register_validity.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkUserRegisterValidity(String str, String type) {
        return iUserService.checkUserRegisterValidity(str, type);
    }

    // retrieve user information from session API
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
    }

    // user password recovery question API
    @RequestMapping(value = "get_password_recovery_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getPasswordRecoveryQuestion(String username) {
        return iUserService.getPasswordRecoveryQuestion(username);
    }

    // check answer to password recovery question API
    @RequestMapping(value = "check_answer_to_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkAnswerToRecoveryQuestion(String username, String question, String answer) {
        return iUserService.checkAnswerToRecoveryQuestion(username,question,answer);
    }

    // user forgotten password reset API
    @RequestMapping(value = "reset_forgotten_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetForgottenPassword(String username, String newPassword, String token) {
        return iUserService.resetForgottenPassword(username,newPassword,token);
    }

    // logged-in user reset password API
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session, String oldPassword, String newPassword) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(oldPassword , newPassword, user);
    }

    // retrieve full user information from DB API
    @RequestMapping(value = "get_full_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> retrieveFullUserInfo(HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，需要强制登录");
        }
        return iUserService.retrieveFullUserInfo(currentUser.getId());
    }


    // update user information by logged-in user API
    @RequestMapping(value = "update_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInfo(HttpSession session, User user) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateUserInfo(user);
        if(response.isSuccess()) {
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }
}
