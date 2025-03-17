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
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Basic test to ensure the processor is instantiable.
 * Full functional testing would require a compiler testing framework.
 */
public class RememberProcessorTest {

    @Test
    public void testProcessorInstantiation() {
        RememberProcessor processor = new RememberProcessor();
        assertNotNull(processor);
    }

    @Test
    public void testRememberAnnotationAvailable() {
        // This verifies that the annotation is available for Java usage
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        Date futureDate = calendar.getTime();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(futureDate);
        
        // This is just a compilation test, not runtime
        Class<?> annotationClass = Remember.class;
        assertNotNull(annotationClass);
    }
}