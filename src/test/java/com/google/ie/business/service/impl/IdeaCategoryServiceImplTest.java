// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.ie.business.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.ie.business.dao.impl.IdeaCategoryDaoImpl;
import com.google.ie.business.domain.IdeaCategory;
import com.google.ie.common.cache.CacheConstants;
import com.google.ie.common.cache.CacheHelper;
import com.google.ie.test.ServiceTest;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Test case for IdeaCategoryServiceImpl class
 * 
 * @author Sachneet
 * 
 */
public class IdeaCategoryServiceImplTest extends ServiceTest {
    IdeaCategoryServiceImpl categoryService;

    @Before
    public void setUp() {
        super.setUp();
        categoryService = new IdeaCategoryServiceImpl();
        categoryService.setIdeaCategoryDao(mock(IdeaCategoryDaoImpl.class));
    }

    @Test
    public void addCategory() {
        IdeaCategory category = new IdeaCategory();
        category.setName("testCategory1");
        category.setDescription("testCategory1 description");
        when(categoryService.getIdeaCategoryDao().saveIdeaCategory(category))
                        .thenReturn(category);

        assertNotNull(categoryService.addIdeaCategory(category));
    }

    @Test
    public void getAllCategories_fromCache() {
        CacheHelper.putObject(CacheConstants.CATEGORY_NAMESPACE, CacheConstants.CATEGORIES, this
                        .getMockCategoriesList());
        List<IdeaCategory> listOfCategoryObjects = categoryService.getAllIdeaCategories();

        assertNotNull(listOfCategoryObjects);
    }

    @Test
    public void getAllCategories_fromDatastore() {
        when(categoryService.getIdeaCategoryDao().getIdeaCategories())
                        .thenReturn(this.getMockCategoriesList());
        List<IdeaCategory> listOfCategoryObjects = categoryService.getAllIdeaCategories();
        assertNotNull(listOfCategoryObjects);
    }

    @Test
    public void getCategoryByName() {
        when(categoryService.getIdeaCategoryDao().getCategoryByName("testCategory")).thenReturn(
                        this.getMockCategoriesList().get(0));
        IdeaCategory category = categoryService.getCategoryByName("testCategory");
        assertNotNull(category);
        assertEquals("testCategory", category.getName());
    }

    /**
     * Creates sample category list
     * 
     * @return a sample list of {@link IdeaCategory} objects
     */
    private LinkedList<IdeaCategory> getMockCategoriesList() {
        IdeaCategory category = new IdeaCategory();
        category.setName("testCategory");
        LinkedList<IdeaCategory> categories = new LinkedList<IdeaCategory>();
        categories.add(category);

        return categories;
    }
}
