/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2023 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.remember.processor;

import com.agorapulse.remember.DoNotMerge;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.Set;

/**
 * Java annotation processor for @DoNotMerge annotation.
 * Prevents code from being merged in pull request environments by failing compilation.
 */
@AutoService(Processor.class)
public class DoNotMergeProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(DoNotMerge.class)) {
            if (isPullRequest()) {
                DoNotMerge doNotMerge = element.getAnnotation(DoNotMerge.class);
                String message = doNotMerge.value();

                if (message == null || message.isEmpty()) {
                    message = "This code should not be merged to the main branch";
                }

                messager.printMessage(Diagnostic.Kind.ERROR, message, element);
            }
        }
        return true;
    }

    private boolean isPullRequest() {
        return isTravisPullRequest() || isGithubActionPullRequest();
    }

    private boolean isGithubActionPullRequest() {
        String githubRef = System.getenv("GITHUB_REF");
        return githubRef != null && githubRef.startsWith("refs/pull/");
    }

    private boolean isTravisPullRequest() {
        String travisPullRequest = System.getenv("TRAVIS_PULL_REQUEST");
        return travisPullRequest != null && !travisPullRequest.isEmpty() && !"false".equals(travisPullRequest);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(DoNotMerge.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        // Try to support the latest version available in the current JVM
        // but fall back to Java 8 if higher versions aren't available
        try {
            // First try to use the highest available version
            return SourceVersion.latest();
        } catch (Exception e) {
            // Fall back to Java 8 if there's any issue
            return SourceVersion.RELEASE_8;
        }
    }
}
