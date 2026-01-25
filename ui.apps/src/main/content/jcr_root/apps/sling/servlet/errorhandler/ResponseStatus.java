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

package apps.sling.servlet.errorhandler;

import com.adobe.cq.sightly.WCMUsePojo;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;

/**
 * Error handler class that sets the HTTP response status to 404 Not Found.
 * This class is not designed for extension and should remain final.
 */
public final class ResponseStatus extends WCMUsePojo {

    private static final int NOT_FOUND_STATUS = HttpServletResponse.SC_NOT_FOUND;

    /**
     * Activates the error handler by setting the response status to 404 Not Found
     * and configuring the content type if not in an include context.
     *
     * @throws Exception if an error occurs during activation
     */
    @Override
    public void activate() throws Exception {
        getResponse().setStatus(NOT_FOUND_STATUS);
        if (getRequest().getAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH) == null) {
            getResponse().setContentType("text/html");
        }
    }
}
