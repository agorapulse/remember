package com.agorapulse.remember;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({
    ElementType.TYPE,
    ElementType.FIELD,
    ElementType.METHOD,
    ElementType.PARAMETER,
    ElementType.CONSTRUCTOR,
    ElementType.LOCAL_VARIABLE,
    ElementType.ANNOTATION_TYPE,
    ElementType.PACKAGE,
    ElementType.TYPE_PARAMETER,
    ElementType.TYPE_USE
})
@GroovyASTTransformationClass("com.agorapulse.remember.DoNotMergeTransformation")
/**
 * <code>@DoNotMerge</code>is an annotation which helps you not to forget any temporary solution on a feature branch
 * which you have introduced into your code base. The code won't compile if the code is running from pull request continuous build.
 *
 */
public @interface DoNotMerge {

    /**
     * @return description why should the code expression should not be merged into the main branch
     */
    String value() default "Do not merge";

}
