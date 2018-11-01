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
@GroovyASTTransformationClass("com.agorapulse.remember.RememberTransformation")
/**
 * <code>@Remember</code>is an annotation which helps you not to forget any temporary solution (aka hacks or quick wins)
 * you have introduced into your code base. You specify the date in the future when you want to revisit the code, e.g. <code>@Remember('2018-12-24)</code>.
 * After this date the code no longer compiles forcing you to re-evaluate if the code is still required or to find
 * more permanent solution.
 */
public @interface Remember {

    /**
     * @return date when the annotated element should be revisited
     */
    String value();

    /**
     * @return format to be used when parsing <code>value</code>
     */
    String format() default RememberTransformation.DEFAULT_FORMAT;

    /**
     * @return description to be shown by the compilation error when the <code>value</code> date is already in the past
     */
    String description() default RememberTransformation.DEFAULT_DESCRIPTION;

    /**
     * @return developer responsible of resoliving the issues associated with this annotation
     */
    String owner() default "";

    /**
     * @return whether the annotation should fail during the CI execution, defaults to <code>false</code>
     */
    boolean ci() default false;

}
