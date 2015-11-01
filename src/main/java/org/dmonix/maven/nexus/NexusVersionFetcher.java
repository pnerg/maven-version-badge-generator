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

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.dmonix.maven.NoSuchArtifactException;

/**
 * Uitility for fetching version information from Nexus. <br>
 * The class will use the REST API provided by Nexus.
 * 
 * @author Peter Nerg
 *
 */
public class NexusVersionFetcher {

    private final String url;
    private String repository = "NOT-SET";
    private String groupId = "NOT-SET";
    private String artifactId = "NOT-SET";

    /**
     * Inhibitive constructor.
     */
    private NexusVersionFetcher(String url) {
        this.url = url;
    }

    /**
     * The root URL to the Nexus server.
     * 
     * @param url
     * @return
     */
    public static NexusVersionFetcher forURL(String url) {
        return new NexusVersionFetcher(url);
    }

    /**
     * The repository in Nexus to find the artifact.
     * 
     * @param repository
     * @return
     */
    public NexusVersionFetcher withRepository(String repository) {
        this.repository = repository;
        return this;
    }

    /**
     * The group id for the artifact.
     * 
     * @param groupId
     * @return
     */
    public NexusVersionFetcher withGroupID(String groupId) {
        this.groupId = groupId;
        return this;
    }

    /**
     * The artifact id for the artifact.
     * 
     * @param artifactId
     * @return
     */
    public NexusVersionFetcher withArtifactID(String artifactId) {
        this.artifactId = artifactId;
        return this;
    }

    /**
     * Gets the latest version of the requested artifact.
     * 
     * @return
     * @throws IOException
     * @throws NoSuchArtifactException
     *             In case no such artifact exists
     */
    public String getLatestVersion() throws IOException, NoSuchArtifactException {
        // build the request URL
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        sb.append("/service/local/artifact/maven/resolve?r=").append(repository);
        sb.append("&g=").append(groupId);
        sb.append("&a=").append(artifactId);
        sb.append("&v=LATEST");

        URL url = new URL(sb.toString());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept-Encoding", "");
        connection.setRequestProperty("Content-Encoding", "NONE");
        connection.connect();

        switch (connection.getResponseCode()) {
        case HTTP_OK:
            String result = readStringFromStream(connection.getInputStream(), connection.getContentLength());
            return getVersionFromResult(result);
        case HTTP_NOT_FOUND:
            throw new NoSuchArtifactException(groupId, artifactId);
        default:
            return null;
        }
    }

    private static String readStringFromStream(InputStream istream, int bytes) throws IOException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < bytes; i++) {
            result.append(Character.toChars(istream.read()));
        }
        return result.toString();
    }

    
    private static String getVersionFromResult(String result) {
        //example of result/response from Nexus
//        <artifact-resolution>
//            <data>
//                <presentLocally>true</presentLocally>
//                <groupId>org.dmonix</groupId> 
//                <artifactId>something</artifactId>
//                <version>1.2</version>
//                <extension>jar</extension>
//                <snapshot>false</snapshot>
//                <snapshotBuildNumber>0</snapshotBuildNumber>
//                <snapshotTimeStamp>0</snapshotTimeStamp>
//                <sha1>6a1ffb41309ff2feb47613942acc02061d35b67e</sha1>
//            <repositoryPath>/org/dmonix/something/1.2/something-1.2.jar</repositoryPath>
//            </data>
//        </artifact-resolution>
        
        String startTag = "<version>";
        int versionStart = result.indexOf(startTag) + startTag.length();
        int versionEnd = result.indexOf("</version>");

        return result.substring(versionStart, versionEnd);
    }

}
