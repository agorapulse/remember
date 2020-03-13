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
import java.util.Optional;

/**
 * AST Transformation for {@link DoNotMerge} annotation.
 */
@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class DoNotMergeTransformation implements ASTTransformation {

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        AnnotationNode annotation = (AnnotationNode) nodes[0];

        Expression valueExpression = annotation.getMember("value");
        String value = Optional.ofNullable(valueExpression).map(Expression::getText).orElse("Do not merge");

        if (isPullRequest()) {
            source.addError(createSyntaxException(annotation, value));
        }
    }

    private boolean isPullRequest() {
        return isTravisPullRequest() || isGithubActionPullRequest();
    }

    private boolean isGithubActionPullRequest() {
        return System.getenv()
            .keySet()
            .stream()
            .filter(key -> key.endsWith("GITHUB_REF"))
            .findAny()
            .map(System::getenv)
            .map(value -> value != null && value.length() > 0 && value.startsWith("refs/pull/"))
            .orElse(false);
    }

    private boolean isTravisPullRequest() {
        return System.getenv()
            .keySet()
            .stream()
            .filter(key -> key.endsWith("PULL_REQUEST"))
            .findAny()
            .map(System::getenv)
            .map(value -> value.length() > 0 && !"false".equals(value))
            .orElse(false);
    }

    private SyntaxException createSyntaxException(AnnotationNode annotation, String message) {
        return new SyntaxException(message, annotation.getLineNumber(), annotation.getColumnNumber(), annotation.getColumnNumber(), annotation.getLastColumnNumber());
    }
}
