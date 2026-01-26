/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.criticalmass.core.it.tests;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.junit.rules.CQAuthorPublishClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;
import org.apache.sling.testing.clients.ClientException;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;


/**
 * Test that some paths exist out-of-the-box on the author service. This test
 * showcases some <a
 * href="https://github.com/adobe/aem-testing-clients/wiki/Best-practices">best
 * practices</a> of the <a
 * href="https://github.com/adobe/aem-testing-clients">AEM Testing Clients</a>.
 */
public class GetPageIT {

    /** HTTP OK status code. */
    private static final int HTTP_OK = 200;

    /**
     * The CQAuthorClassRule represents an author service. The rule will read
     * the hostname and port of the author service from the system properties
     * passed to the tests.
     */
    @ClassRule
    public static final CQAuthorPublishClassRule CQ_BASE_CLASS_RULE =
            new CQAuthorPublishClassRule();

    /**
     * CQRule decorates your test and adds additional functionality on top of
     * it, like session stickyness, test filtering and identification of the
     * test on the remote service.
     */
    @Rule
    public CQRule cqBaseRule = new CQRule(CQ_BASE_CLASS_RULE.authorRule,
            CQ_BASE_CLASS_RULE.publishRule);

    /** CQClient instance for admin user on author service. */
    private static CQClient adminAuthor;

    /** CQClient instance for admin user on publish service. */
    private static CQClient adminPublish;

    /**
     * Thanks to the CQAuthorClassRule, we can create two CQClient instances
     * bound to the admin user on both the author and publish service.
     *
     * @throws ClientException if client initialization fails
     */
    @BeforeClass
    public static void beforeClass() throws ClientException {
        adminAuthor = CQ_BASE_CLASS_RULE.authorRule.getAdminClient(
                CQClient.class);
        adminPublish = CQ_BASE_CLASS_RULE.publishRule.getAdminClient(
                CQClient.class);
    }

    /**
     * Verifies that the homepage exists on author.
     *
     * @throws ClientException if request fails
     */
    @Test
    public void testHomePageAuthor() throws ClientException {
        adminAuthor.doGet("/", HTTP_OK);
    }

    /**
     * Verifies that the sites console exists on author.
     *
     * @throws ClientException if request fails
     */
    @Test
    public void testSitesAuthor() throws ClientException {
        adminAuthor.doGet("/sites.html", HTTP_OK);
    }

    /**
     * Verifies that the assets console exists on author.
     *
     * @throws ClientException if request fails
     */
    @Test
    public void testAssetsAuthor() throws ClientException {
        adminAuthor.doGet("/assets.html", HTTP_OK);
    }

    /**
     * Verifies that the projects console exists on author.
     *
     * @throws ClientException if request fails
     */
    @Test
    public void testProjectsAuthor() throws ClientException {
        adminAuthor.doGet("/projects.html", HTTP_OK);
    }

}
