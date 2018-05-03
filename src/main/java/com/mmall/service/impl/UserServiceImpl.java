package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * Created by Zhang Chen
 */
@Service("iUserService")
public class UserServiceImpl  implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0) {
            return  ServerResponse.createByErrorMessage("用户名不存在");
        }

        //to do password md5 encryption
        String encryptedPassword = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,encryptedPassword);
        if(user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    public ServerResponse<String> register(User user) {
        // validate username
        ServerResponse response = this.checkUserRegisterValidity(user.getUsername(), Const.USERNAME);
        if(!response.isSuccess()) {
            return response;
        }
        // validate user email
        response = this.checkUserRegisterValidity(user.getEmail(), Const.EMAIL);
        if(!response.isSuccess()) {
            return response;
        }
        // set up user role
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //password MD5 encryption
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        // insert user
        int resultCount = userMapper.insert(user);
        if(resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    public ServerResponse<String> checkUserRegisterValidity(String str, String type) {
        if(StringUtils.isNotBlank(type)) {
            // validate
            if(Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0) {
                    return  ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0) {
                    return  ServerResponse.createByErrorMessage("Email已存在");
                }
            }
        }
        else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    public ServerResponse<String> getPasswordRecoveryQuestion(String username) {
        ServerResponse response = this.checkUserRegisterValidity(username, Const.USERNAME);
        if(response.isSuccess()){
            // user does not exist
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码问题为空");
    }

    public ServerResponse<String> checkAnswerToRecoveryQuestion(String username, String question, String answer) {
        int resultCount =userMapper.checkAnswerToRecoveryQuestion(username,question,answer);
        if(resultCount>0) {
            String token = UUID.randomUUID().toString();
            TokenCache.setToken(TokenCache.TOKEN_PREFIX+username, token);
            return ServerResponse.createBySuccess(token);
        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    public ServerResponse<String> resetForgottenPassword(String username, String newPassword, String token) {
        if(StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("参数错误，需要传递Token");
        }
        // user does not exist
        ServerResponse response = this.checkUserRegisterValidity(username, Const.USERNAME);
        if(response.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String cachedToken = TokenCache.getToken(TokenCache.TOKEN_PREFIX+username);
        //check token validity
        if(StringUtils.isBlank(cachedToken)) {
            return ServerResponse.createByErrorMessage("Token无效或者已经过期");
        }
        if(StringUtils.equals(token, cachedToken)) {
            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);
            if(rowCount > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        }
        else {
            return ServerResponse.createByErrorMessage("Token错误，请重新获取重置密码的Token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    public ServerResponse<String> resetPassword(String oldPassword, String newPassword, User user) {
        // prevent unauthorized password reset by user of equal privileges
        // need to check whether the old password in DB match that of the current user
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword), user.getId());
        if(resultCount == 0) {
            return ServerResponse.createByErrorMessage("指定用户旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    public ServerResponse<User> retrieveFullUserInfo(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null) {
            ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    public ServerResponse<User> updateUserInfo(User user) {
        //cannot update username
        //validate email
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if(resultCount > 0) {
            return ServerResponse.createByErrorMessage("Email已经存在，请更换Email再尝试更新");
        }
        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setEmail(user.getEmail());
        updatedUser.setPhone(user.getPhone());
        updatedUser.setQuestion(user.getAnswer());
        updatedUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(updatedUser);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("个人信息更新成功", updatedUser);
        }
        return ServerResponse.createByErrorMessage("个人信息更新失败");
    }

    // backend-related service implementations

    /**
     * validate whether the specified user is an administrator
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user) {
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}
