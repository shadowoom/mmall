package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by Zhang Chen
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * Controller for adding category
     * @param session
     * @param categoryName
     * @param parentId
     * @HttpRequest POST
     * @return ServerResponse<String>
     */
    @RequestMapping(value="add_category.do", method=RequestMethod.POST)
    @ResponseBody // return jackson serialization
    public ServerResponse<String> addCategory(HttpSession session, String categoryName,
                                              @RequestParam(value="parentId",defaultValue = "0") int parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        // validate whether the user is an administrator
        if(iUserService.checkAdminRole(user).isSuccess()) {
            //add category logic
            return iCategoryService.addCategory(categoryName, parentId);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    /**
     * Controller for updating category name controller
     * @param session
     * @param categoryId
     * @param categoryName
     * @HttpRequest POST
     * @return ServerResponse<String>
     */
    @RequestMapping(value="set_category_name.do", method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        // validate whether the user is an administrator
        if(iUserService.checkAdminRole(user).isSuccess()) {
            //update category name logic
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    /**
     * Controller for getting a list of sub categories
     * @param session
     * @param categoryId
     * @HttpRequest GET
     * @return ServerResponse<List<Category>>
     */
    @RequestMapping(value="get_subcategory.do", method=RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Category>> getSubCategory(
            HttpSession session,
            @RequestParam(value="categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iCategoryService.getSubCategory(categoryId);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    /**
     * Controller for getting a category and all its sub-categories
     * @param session
     * @param categoryId
     * @HttpRequest GET
     * @return ServerResponse<List<Integer>>
     */
    @RequestMapping(value="get_deep_category.do", method=RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Integer>> getCategoryAndSubCategories(
            HttpSession session,
            @RequestParam(value="categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登录");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iCategoryService.getCategoryAndSubCategories(categoryId);
        }
        else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }
}
