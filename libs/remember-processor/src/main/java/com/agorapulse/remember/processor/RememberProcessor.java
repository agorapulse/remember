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

import com.agorapulse.remember.Remember;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Java annotation processor for @Remember annotation.
 * Ensures code with expired expiration dates fails compilation.
 */
@AutoService(Processor.class)
public class RememberProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Remember.class)) {
            Remember remember = element.getAnnotation(Remember.class);
            
            String dateStr = remember.value();
            String format = remember.format();
            String description = remember.description();
            String owner = remember.owner();
            boolean ci = remember.ci();
            
            // Skip checking if ci is false and running in a CI environment
            if (!ci && isRunningInCI()) {
                continue;
            }
            
            try {
                DateFormat dateFormat = new SimpleDateFormat(format);
                dateFormat.setLenient(false);
                
                Date dateToRemember = dateFormat.parse(dateStr);
                Date currentDate = new Date();
                
                if (dateToRemember.before(currentDate)) {
                    String message = buildErrorMessage(description, dateStr, owner);
                    messager.printMessage(Diagnostic.Kind.ERROR, message, element);
                }
            } catch (ParseException e) {
                messager.printMessage(
                    Diagnostic.Kind.ERROR, 
                    String.format("Unable to parse date '%s' using format '%s': %s", 
                        dateStr, format, e.toString()),
                    element
                );
            }
        }
        return true;
    }

    private String buildErrorMessage(String description, String dateStr, String owner) {
        StringBuilder message = new StringBuilder();
        message.append("Expiration date ").append(dateStr).append(" has passed");
        
        if (description != null && !description.isEmpty()) {
            message.append(": ").append(description);
        }
        
        if (owner != null && !owner.isEmpty()) {
            message.append(" (owner: ").append(owner).append(")");
        }
        
        return message.toString();
    }

    private boolean isRunningInCI() {
        return Boolean.TRUE.toString().equalsIgnoreCase(System.getenv("CI"))
            || System.getenv("GITHUB_WORKFLOW") != null
            || System.getenv("TRAVIS") != null
            || System.getenv("JENKINS_URL") != null
            || Boolean.TRUE.toString().equalsIgnoreCase(System.getProperty("ci"));
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Remember.class.getCanonicalName());
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