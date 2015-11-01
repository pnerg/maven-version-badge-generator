/**
 *  Copyright 2015 Peter Nerg
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
package org.dmonix.maven.nexus;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dmonix.maven.NoSuchArtifactException;
import org.dmonix.maven.ImageGenerator;

/**
 * 
 * @author Peter Nerg
 *
 */
public class NexusVersionFetcherServlet extends HttpServlet {

    private static final long serialVersionUID = -593994632007476263L;

    private String defaultRepository = "";
    private String nexusURL = "";
    private String imageFormat;
    private String fontType;
    private int fontSize;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        defaultRepository = getInitParameter(config, "default-repository", "");
        nexusURL = getInitParameter(config, "nexus-url", "");
        imageFormat = getInitParameter(config, "image-format", "png");
        fontType = getInitParameter(config, "font-type", "Arial");
        fontSize = Integer.parseInt(getInitParameter(config, "font-size", "14"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String groupId = req.getParameter("g");
        String artifactId = req.getParameter("a");
        String repository = getRequestParameter(req, "r", defaultRepository);

        NexusVersionFetcher versionFetcher = NexusVersionFetcher.forURL(nexusURL).withRepository(repository).withArtifactID(artifactId).withGroupID(groupId);

        String latestVersion;
        try {
            latestVersion = versionFetcher.getLatestVersion();
        } catch (NoSuchArtifactException e) {
            latestVersion = "N/A";
        }

        resp.setContentType("image/" + imageFormat);
        ImageGenerator.forText(latestVersion).withFont(fontType).withFontSize(fontSize).createImage(resp.getOutputStream());
    }

    private static String getInitParameter(ServletConfig config, String name, String defaultVal) {
        return config.getInitParameter(name) != null ? config.getInitParameter(name) : defaultVal;
    }

    private static String getRequestParameter(HttpServletRequest req, String name, String defaultVal) {
        return req.getParameter(name) != null ? req.getParameter(name) : defaultVal;
    }
}
