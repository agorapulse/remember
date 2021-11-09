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
package com.agorapulse.remember

import groovy.test.GroovyAssert
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException
import spock.lang.Specification

import static com.github.stefanbirkner.systemlambda.SystemLambda.*

@SuppressWarnings('TrailingWhitespace')
class DoNotMergeSpec extends Specification {

    public static final String PR_ENV_VAR_NAME = 'GITHUB_REF'
    @Rule EnvironmentVariables environmentVariables = new EnvironmentVariables()

    void 'the annotation is ignored by default'() {
        when:
            // to pass PR build for this library
            environmentVariables.clear(PR_ENV_VAR_NAME)

            // language=Groovy
            GroovyAssert.assertScript '''
                import com.agorapulse.remember.DoNotMerge

                @DoNotMerge
                class Subject { }

                true
            '''
        then:
            noExceptionThrown()
    }

    void 'the annotation is ignored of the pull request env var is false'() {
        when:
            // to pass PR build for this library
            environmentVariables.clear(PR_ENV_VAR_NAME)

            // language=Groovy
            GroovyAssert.assertScript '''
                import com.agorapulse.remember.DoNotMerge

                @DoNotMerge
                class Subject { }

                true
            '''
        then:
            noExceptionThrown()
    }

    void 'error is reported on PR build'() {
        when:
            environmentVariables.set(PR_ENV_VAR_NAME, 'refs/pull/123456')
            // language=Groovy
            GroovyAssert.assertScript '''
                import com.agorapulse.remember.DoNotMerge

                @DoNotMerge
                class Subject { }

                true
            '''
        then:
            MultipleCompilationErrorsException e = thrown(MultipleCompilationErrorsException)
            assertMessage(e, 'Do not merge @ line 4, column 17.')
    }

    void 'error is reported on PR build - with details'() {
        when:
            environmentVariables.set(PR_ENV_VAR_NAME, 'refs/pull/123456')
            // language=Groovy
            GroovyAssert.assertScript """
                import com.agorapulse.remember.DoNotMerge

                @DoNotMerge('This will break everything!')
                class Subject { }

                true
            """
        then:
            MultipleCompilationErrorsException e = thrown(MultipleCompilationErrorsException)
            assertMessage(e, 'This will break everything! @ line 4, column 17.')
    }

    boolean assertMessage(MultipleCompilationErrorsException multipleCompilationErrorsException, String message) {
        assert multipleCompilationErrorsException.errorCollector
        assert multipleCompilationErrorsException.errorCollector.errorCount == 1
        assert multipleCompilationErrorsException.errorCollector.errors.first() instanceof SyntaxErrorMessage

        SyntaxException exception = multipleCompilationErrorsException.errorCollector.errors.first().cause
        assert exception.message == message

        return true
    }

}
