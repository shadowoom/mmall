package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by Zhang Chen
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     * Controller for inserting or updating a product
     * @param session
     * @param product
     * @HttpRequest POST
     * @return ServerResponse<String>
     */
    @RequestMapping(value = "save.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse saveProduct(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录管理员账号");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            //增加产品的业务逻辑
            return iProductService.saveOrUpdateProduct(product);
        }
        else {
            return ServerResponse.createByError().createByErrorMessage("无权限操作");
        }
    }

    /**
     * Controller for updating product status
     * @param session
     * @param productId
     * @param status
     * @HttpRequest POST
     * @return ServerResponse<String>
     */
    @RequestMapping(value = "set_sale_status.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录管理员账号");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.setSaleStatus(productId, status);
        }
        else {
            return ServerResponse.createByError().createByErrorMessage("无权限操作");
        }
    }

    /**
     * Controller for getting details of a product
     * @param session
     * @param productId
     * @HttpRequest GET
     * @return ServerResponse<ProductDetailVo>
     */
    @RequestMapping(value = "get_product_detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getProductDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录管理员账号");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.manageProductDetail(productId);
        }
        else {
            return ServerResponse.createByError().createByErrorMessage("无权限操作");
        }
    }

    /**
     * Controller for getting the list of products
     * @param session
     * @param pageNum
     * @param pageSize
     * @HttpRequest GET
     * @return ServerResponse<PageInfo>
     */
    @RequestMapping(value = "list.do", method=RequestMethod.GET)
    @ResponseBody
    public ServerResponse getProductList(HttpSession session,
                                         @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录管理员账号");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.getProductList(pageNum, pageSize);
        }
        else {
            return ServerResponse.createByError().createByErrorMessage("无权限操作");
        }
    }

    /**
     * Controller for searching for matching list of products
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @HttpRequest GET
     * @return ServerResponse<PageInfo>
     */
    @RequestMapping(value = "search.do", method=RequestMethod.GET)
    @ResponseBody
    public ServerResponse searchProduct(HttpSession session, String productName, Integer productId,
                                         @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录管理员账号");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
        }
        else {
            return ServerResponse.createByError().createByErrorMessage("无权限操作");
        }
    }

    /**
     * Controller for file upload
     * @param session
     * @param request
     * @param file
     * @HttpRequest POST
     * @return ServerResponse<String>
     */

    @RequestMapping(value = "upload.do", method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(HttpSession session,HttpServletRequest request,
                                 @RequestParam(value = "upload_file", required = false) MultipartFile file) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),
                    "用户未登录，请登录管理员账号");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);
        }
        else {
            return ServerResponse.createByError().createByErrorMessage("无权限操作");
        }
    }

    /**
     * Controller for rich text upload
     * @param session
     * @param request
     * @param file
     * @HttpRequest POST
     * @return Map
     */

    @RequestMapping(value = "richtext_img_upload.do", method=RequestMethod.POST)
    @ResponseBody
    public Map richTextImgUpload(HttpSession session,HttpServletRequest request, HttpServletResponse response,
                                 @RequestParam(value = "upload_file", required = false) MultipartFile file) {
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "用户未登录，请登录管理员账号");
            return resultMap;
        }
        // rich text has its own requirements for the return value
        // follow simditor's requirements
        if(iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            if(StringUtils.isBlank(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);
            response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
            return resultMap;
        }
        else {
            resultMap.put("success", false);
            resultMap.put("msg", "无权限操作");
            return resultMap;
        }
    }

}
