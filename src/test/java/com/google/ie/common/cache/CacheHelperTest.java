// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.ie.common.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import com.google.ie.test.TestEnvironment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Test cases to test each cases of methods in CustomCacheManager class.
 * 
 * @author asirohi
 * 
 */
public class CacheHelperTest {
    private static final String TEST_NAMESPACE = "test";

    @Before
    public void setUp() throws Exception {
        ApiProxy.setEnvironmentForCurrentThread(new TestEnvironment());
        ApiProxy.setDelegate(new ApiProxyLocalImpl(new File(".")) {
        });
    }

    @After
    public void tearDown() throws Exception {
        // not strictly necessary to null these out but there's no harm either
        ApiProxy.setDelegate(null);
        ApiProxy.setEnvironmentForCurrentThread(null);
    }

    @Test
    public void putObject() {

        try {
            CacheHelper.putObject(TEST_NAMESPACE, "key1", "value1");
            CacheHelper.putObject(TEST_NAMESPACE, "key2", "value2");

        } catch (IllegalArgumentException iae) {
            fail("Non serializable object: Should not occur");
        }
        assertEquals(true, CacheHelper.containsObject(TEST_NAMESPACE, "key1"));
    }

    @Test
    public void putObjectWithExpiration() {

        try {
            CacheHelper.putObject(TEST_NAMESPACE, "key1", "value1", 100);
        } catch (IllegalArgumentException iae) {
            fail("Non serializable object: Should not occur");
        }
        assertEquals(true, CacheHelper.containsObject(TEST_NAMESPACE, "key1"));
    }

    @Test
    public void getObject() {
        CacheHelper.putObject(TEST_NAMESPACE, "key1", "value1");

        assertNotNull("key1 should not be null", CacheHelper.getObject(TEST_NAMESPACE, "key1"));
        assertEquals("Wrong name value", "value1", CacheHelper.getObject(TEST_NAMESPACE, "key1"));
    }

    @Test
    public void contains() {
        CacheHelper.putObject(TEST_NAMESPACE, "key1", "value1");
        assertEquals(true, CacheHelper.containsObject(TEST_NAMESPACE, "key1"));
    }

    @Test
    public void delete() {
        CacheHelper.putObject(TEST_NAMESPACE, "key1", "value1");
        CacheHelper.deleteObject(TEST_NAMESPACE, "key1");
        assertEquals(false, CacheHelper.containsObject(TEST_NAMESPACE, "key1"));
    }

    @Test
    public void clearAll() {
        CacheHelper.putObject(TEST_NAMESPACE, "key1", "value1");
        CacheHelper.putObject(TEST_NAMESPACE, "key2", "value2");
        CacheHelper.putObject(TEST_NAMESPACE, "key3", "value3");

        CacheHelper.clearAllObjects();

        assertEquals(false, CacheHelper.containsObject(TEST_NAMESPACE, "key1"));
        assertEquals(false, CacheHelper.containsObject(TEST_NAMESPACE, "key2"));
        assertEquals(false, CacheHelper.containsObject(TEST_NAMESPACE, "key3"));
    }

}
