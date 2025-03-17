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
package com.agorapulse.remember.example;

import com.agorapulse.remember.Remember;
import com.agorapulse.remember.DoNotMerge;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Example of using Remember and DoNotMerge annotations in Java code.
 * 
 * For this to work during compilation, you need to include remember-processor:
 * 
 * ```gradle
 * dependencies {
 *     implementation 'com.agorapulse:remember:1.0.0'
 *     annotationProcessor 'com.agorapulse:remember-processor:1.0.0'
 * }
 * ```
 */
public class JavaExample {

    // Calculate a date one year in the future to avoid compilation errors
    private static final String FUTURE_DATE;
    
    static {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        Date futureDate = calendar.getTime();
        FUTURE_DATE = new SimpleDateFormat("yyyy-MM-dd").format(futureDate);
    }

    /**
     * This class uses the Remember annotation with a future date.
     * The build will succeed because the expiration date is in the future.
     */
    @Remember(
        value = FUTURE_DATE, 
        description = "Replace this temporary solution with a proper implementation",
        owner = "johnsmith"
    )
    public static class TemporaryImplementation {
        public String doSomething() {
            return "This is a temporary implementation";
        }
    }

    /**
     * This class uses the DoNotMerge annotation.
     * The build will fail if running in a pull request environment.
     */
    @DoNotMerge("This is experimental code that should not be merged to the main branch")
    public static class ExperimentalCode {
        public String doSomethingExperimental() {
            return "This is experimental and should not be merged";
        }
    }

    public static void main(String[] args) {
        System.out.println(new TemporaryImplementation().doSomething());
        System.out.println(new ExperimentalCode().doSomethingExperimental());
    }
}