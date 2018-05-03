package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by Zhang Chen
 */
public interface ICategoryService {

    /**
     * Service for adding category
     * @param categoryName
     * @param parentId
     * @return ServerResponse<String>
     */
    ServerResponse<String> addCategory(String categoryName, Integer parentId);

    /**
     * Service for updating category
     * @param categoryId
     * @param categoryName
     * @return ServerResponse<String>
     */
    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    /**
     * Service for getting a list of sub categories
     * @param categoryId
     * @return ServerResponse<List<Category>>
     */
    ServerResponse<List<Category>> getSubCategory(Integer categoryId);

    /**
     * Service for searching recursively the ids of all categories
     * in the sub-tree of a particular category based on its id
     * @param categoryId
     * @return ServerResponse<List<Integer>>
     */
    ServerResponse<List<Integer>> getCategoryAndSubCategories(Integer categoryId);

}
