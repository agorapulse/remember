package com.agorapulse.remember

import groovy.test.GroovyAssert
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException
import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

class DoNotMergeSpec extends Specification {

    public static final String PR_ENV_VAR_NAME = 'TRAVIS_PULL_REQUEST'
    @Rule EnvironmentVariables environmentVariables = new EnvironmentVariables()

    void 'the annotation is ignored by default'() {
        when:
            // to pass PR build for this library
            environmentVariables.clear(PR_ENV_VAR_NAME)

            // language=Groovy
            GroovyAssert.assertScript """
                import com.agorapulse.remember.DoNotMerge

                @DoNotMerge   
                class Subject { }
                
                true
            """
        then:
            noExceptionThrown()
    }

    void 'the annotation is ignored of the pull request env var is false'() {
        when:
            // to pass PR build for this library
            environmentVariables.set(PR_ENV_VAR_NAME, 'false')

            // language=Groovy
            GroovyAssert.assertScript """
                import com.agorapulse.remember.DoNotMerge

                @DoNotMerge   
                class Subject { }
                
                true
            """
        then:
            noExceptionThrown()
    }

    void 'error is reported on PR build'() {
        when:
            environmentVariables.set(PR_ENV_VAR_NAME, '123456')
            // language=Groovy
            GroovyAssert.assertScript """
                import com.agorapulse.remember.DoNotMerge

                @DoNotMerge 
                class Subject { }
                
                true
            """
        then:
            MultipleCompilationErrorsException e = thrown(MultipleCompilationErrorsException)
            assertMessage(e, 'Do not merge @ line 4, column 17.')
    }

    void 'error is reported on PR build - with details'() {
        when:
            environmentVariables.set(PR_ENV_VAR_NAME, '123456')
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
