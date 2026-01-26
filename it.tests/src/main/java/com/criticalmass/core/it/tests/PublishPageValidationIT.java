/*
 *  Copyright 2020 Adobe Systems Incorporated
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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingHttpResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

/**
 * Validates pages on publish and makes sure that the page renders completely
 * and also validates all linked resources (images, clientlibs etc).
 */
public class PublishPageValidationIT {

    /** HTTP OK status code. */
    private static final int HTTP_OK = 200;

    /** The page to test. */
    private static final String HOMEPAGE = "/";

    /** List files which do return a zerobyte response body. */
    private static final List<String> ZEROBYTEFILES = Arrays.asList();

    /** Logger instance. */
    private static final org.slf4j.Logger LOG = LoggerFactory
            .getLogger(PublishPageValidationIT.class);

    /** CQ base class rule for author and publish instances. */
    @ClassRule
    public static final CQAuthorPublishClassRule CQ_BASE_CLASS_RULE =
            new CQAuthorPublishClassRule(true);

    /** CQ rule for publish service. */
    @Rule
    public CQRule cqBaseRule = new CQRule(CQ_BASE_CLASS_RULE.publishRule);

    /** Admin client for publish instance. */
    private static HtmlUnitClient adminPublish;

    /**
     * Set up test environment before class.
     *
     * @throws ClientException if client initialization fails
     */
    @BeforeClass
    public static void beforeClass() throws ClientException {
        adminPublish = CQ_BASE_CLASS_RULE.publishRule
                .getAdminClient(CQClient.class)
                .adaptTo(HtmlUnitClient.class);
    }

    /**
     * Clean up after class execution.
     */
    @AfterClass
    public static void afterClass() {
        // As of 2022/10/13, AEM declares 'org.apache.commons.io.IOUtils
        // .closeQuietly' as deprecated,
        // even though the function has been un-deprecated again in version
        // 2.9.0 of 'commons-io'
        // (https://issues.apache.org/jira/browse/IO-504);
        // thus a try-catch is used instead.
        try {
            adminPublish.close();
        } catch (IOException ignored) {
            // Ignore IOException
        }
    }

    /**
     * Test method to validate homepage.
     *
     * @throws ClientException if client error occurs
     * @throws IOException if IO error occurs
     * @throws URISyntaxException if URI syntax error occurs
     */
    @Test
    @Ignore
    public void validateHomepage() throws ClientException, IOException,
            URISyntaxException {
        String path = HOMEPAGE;
        verifyPage(adminPublish, path);
        verifyLinkedResources(adminPublish, path);
    }

    /**
     * Verifies that a page returns HTTP 200.
     *
     * @param client the client to use
     * @param path the path to verify
     * @throws ClientProtocolException if protocol error occurs
     * @throws IOException if IO error occurs
     */
    private static void verifyPage(final HtmlUnitClient client,
            final String path) throws ClientProtocolException, IOException {
        URI baseURI = client.getUrl();
        LOG.info("Using {} as baseURL", baseURI.toString());
        HttpGet get = new HttpGet(baseURI.toString() + path);
        org.apache.http.HttpResponse validationResponse = client
                .execute(get);
        assertEquals("Request to [" + get.getURI().toString()
                + "] does not return expected returncode 200",
                HTTP_OK, validationResponse.getStatusLine().getStatusCode());
    }

    /**
     * Verifies all linked resources in a page.
     *
     * @param client the client to use
     * @param path the path to verify
     * @throws ClientException if client error occurs
     * @throws IOException if IO error occurs
     * @throws URISyntaxException if URI syntax error occurs
     */
    private static void verifyLinkedResources(final HtmlUnitClient client,
            final String path) throws ClientException, IOException,
            URISyntaxException {
        List<URI> references = client.getResourceRefs(path);
        assertTrue(path + " does not contain any references!",
                references.size() > 0);
        for (URI ref : references) {
            if (isSameOrigin(client.getUrl(), ref)) {
                LOG.info("verifying linked resource {}", ref.toString());
                SlingHttpResponse response = client.doGet(ref.getPath());
                int statusCode = response.getStatusLine().getStatusCode();
                int responseSize = response.getContent().length();
                assertEquals("Unexpected status returned from [" + ref + "]",
                        HTTP_OK, statusCode);
                if (!ZEROBYTEFILES.stream()
                        .anyMatch(s -> ref.getPath().startsWith(s))) {
                    if (responseSize == 0) {
                        LOG.warn("Empty response body from [" + ref.getPath()
                                + "], please validate if this is correct");
                    }
                }
            } else {
                LOG.info("skipping linked resource from another domain {}",
                        ref.toString());
            }
        }
    }

    /**
     * Checks if two URIs have the same origin.
     *
     * @param uri1 first URI
     * @param uri2 second URI
     * @return true if two URI come from the same host, port and use the same
     *         scheme
     */
    private static boolean isSameOrigin(final URI uri1, final URI uri2) {
        if (!uri1.getScheme().equals(uri2.getScheme())) {
            return false;
        } else {
            return uri1.getAuthority().equals(uri2.getAuthority());
        }
    }


}
