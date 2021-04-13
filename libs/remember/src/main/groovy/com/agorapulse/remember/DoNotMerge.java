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
