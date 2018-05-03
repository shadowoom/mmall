package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

/**
 * Created by Zhang Chen
 */
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService{

    @Autowired
    private CategoryMapper categoryMapper;

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    /**
     * Service implementation for adding category
     * @param categoryName
     * @param parentId
     * @return ServerResponse<String>
     */
    public ServerResponse<String> addCategory(String categoryName, Integer parentId) {
        if(parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true); // the category is usable

        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    /**
     * Service implementation for updating category
     * @param categoryId
     * @param categoryName
     * @return ServerResponse<String>
     */
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if(categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0) {
            return ServerResponse.createBySuccessMessage("更新品类名字成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名字失败");
    }

    /**
     * Service implementation for getting a list of sub categories
     * @param categoryId
     * @return ServerResponse<String>
     */
    public ServerResponse<List<Category>> getSubCategory(Integer categoryId) {
        List<Category> categoryList = categoryMapper.selectSubCategoryByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)) {
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * Service implementation for searching recursively the ids of all categories
     * in the sub-tree of a particular category based on its id
     * @param categoryId
     * @return ServerResponse<List<Integer>>
     */
    public ServerResponse<List<Integer>> getCategoryAndSubCategories(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        retrieveCategoryOfAllLevelsById(categorySet, categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null) {
            for(Category categoryItem : categorySet) {
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    /**
     * recursive algorithm to obtain categories of all sub-levels
     * @param categorySet
     * @param categoryId
     * @return Set<Category>
     */
    private Set<Category> retrieveCategoryOfAllLevelsById(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null) {
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.selectSubCategoryByParentId(categoryId);
        for(Category categoryItem : categoryList) {
            retrieveCategoryOfAllLevelsById(categorySet, categoryItem.getId());
        }
        return categorySet;
    }

}
