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
