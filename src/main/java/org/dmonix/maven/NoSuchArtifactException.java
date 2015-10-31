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

package org.dmonix.maven;

/**
 * Exception marking that a particular artifact did not exist.
 * 
 * @author Peter Nerg
 *
 */
public final class NoSuchArtifactException extends Exception {

    private static final long serialVersionUID = 827511105148616576L;

    public NoSuchArtifactException(String groupID, String artifactID) {
        super("Failed to find artifact [" + groupID + "]:[" + artifactID + "]");
    }

    public NoSuchArtifactException(String groupID, String artifactID, Throwable cause) {
        super("Failed to find artifact [" + groupID + "]:[" + artifactID + "]", cause);
    }
}
