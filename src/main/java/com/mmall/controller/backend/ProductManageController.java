package com.mmall.controller.backend;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * Created by Zhang Chen
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    public ServerResponse productSave(HttpSession session, Product product) {

    }

}
