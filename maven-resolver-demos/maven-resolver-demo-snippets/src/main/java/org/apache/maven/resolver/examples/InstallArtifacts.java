/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.resolver.examples;

import java.io.File;

import org.apache.maven.resolver.examples.util.Booter;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.util.artifact.SubArtifact;

/**
 * Installs a JAR and its POM to the local repository.
 */
public class InstallArtifacts {

    /**
     * Main.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        System.out.println("------------------------------------------------------------");
        System.out.println(InstallArtifacts.class.getSimpleName());

        RepositorySystem system = Booter.newRepositorySystem(Booter.selectFactory(args));

        RepositorySystemSession session = Booter.newRepositorySystemSession(system);

        Artifact jarArtifact =
                new DefaultArtifact("test", "org.apache.maven.resolver.examples", "", "jar", "0.1-SNAPSHOT");
        jarArtifact = jarArtifact.setFile(new File("src/main/data/demo.jar"));

        Artifact pomArtifact = new SubArtifact(jarArtifact, "", "pom");
        pomArtifact = pomArtifact.setFile(new File("pom.xml"));

        InstallRequest installRequest = new InstallRequest();
        installRequest.addArtifact(jarArtifact).addArtifact(pomArtifact);

        system.install(session, installRequest);
    }
}
