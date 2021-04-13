/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2019-2021 Agorapulse.
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
package com.agorapulse.remember;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * AST Transformation for {@link Remember} annotation.
 */
@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class RememberTransformation implements ASTTransformation {

    static final String DEFAULT_FORMAT = "yyyy-MM-dd";
    static final String DEFAULT_DESCRIPTION = "Please, make sure the annotated element is still valid for your codebase";

    private static final String CI_ENV_VAR = "CI";
    private static final String CI_SYSTEM_PROPERTY = "ci";
    private static final String GITHUB_WORKFLOW_ENV_VAR = "GITHUB_WORKFLOW";

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        AnnotationNode annotation = (AnnotationNode) nodes[0];

        Expression valueExpression = annotation.getMember("value");
        String value = valueExpression.getText();

        Expression formatExpression = annotation.getMember("format");
        String format = formatExpression != null ? formatExpression.getText() : DEFAULT_FORMAT;

        Expression descriptionExpression = annotation.getMember("description");
        String description = descriptionExpression != null ? descriptionExpression.getText() : DEFAULT_DESCRIPTION;

        Expression ciExpression = annotation.getMember("ci");
        boolean ci = ciExpression != null && Boolean.TRUE.toString().equals(ciExpression.getText());

        if (!ci && isRunningCI()) {
            return;
        }

        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            Date dateToRemember = dateFormat.parse(value);
            if (dateToRemember.getTime() < System.currentTimeMillis()) {
                source.addError(createSyntaxException(annotation, description));
            }
        } catch (ParseException e) {
            source.addError(createSyntaxException(annotation, String.format("Unable to parse date '%s' using format '%s':%n%s", value, format, e.toString())));
        }
    }

    private boolean isRunningCI() {
        return Boolean.TRUE.toString().equals(System.getenv(CI_ENV_VAR))
            || System.getenv(GITHUB_WORKFLOW_ENV_VAR) != null
            || Boolean.TRUE.toString().equals(System.getProperty(CI_SYSTEM_PROPERTY));
    }

    private SyntaxException createSyntaxException(AnnotationNode annotation, String message) {
        return new SyntaxException(message, annotation.getLineNumber(), annotation.getColumnNumber(), annotation.getColumnNumber(), annotation.getLastColumnNumber());
    }
}
