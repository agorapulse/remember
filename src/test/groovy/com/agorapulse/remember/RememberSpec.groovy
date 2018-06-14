package com.agorapulse.remember

import groovy.test.GroovyAssert
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException
import spock.lang.Specification

class RememberSpec extends Specification {

    void 'remember in future is ignored'() {
        when:
            // language=Groovy
            GroovyAssert.assertScript """
                import com.agorapulse.remember.Remember

                @Remember('2999-01-01')   
                class Subject { }
                
                true
            """
        then:
            noExceptionThrown()
    }

    void 'remember in past raises exception'() {
        when:
            // language=Groovy
            GroovyAssert.assertScript """
                import com.agorapulse.remember.Remember

                @Remember('1999-01-01')   
                class Subject { }
                
                true
            """
        then:
            MultipleCompilationErrorsException e = thrown(MultipleCompilationErrorsException)
            assertMessage(e, 'Please, make sure the annotated element is still valid for your codebase @ line 4, column 17.')
    }

    void 'remember in past raises exception (different format)'() {
        when:
            // language=Groovy
            GroovyAssert.assertScript """
                import com.agorapulse.remember.Remember

                @Remember(value = '19990101', format = 'yyyyMMdd')   
                class Subject { }
                
                true
            """
        then:
            MultipleCompilationErrorsException e = thrown(MultipleCompilationErrorsException)
            assertMessage(e, 'Please, make sure the annotated element is still valid for your codebase @ line 4, column 17.')
    }

    void 'remember in past raises exception (different description)'() {
        when:
            // language=Groovy
            GroovyAssert.assertScript """
                import com.agorapulse.remember.Remember

                @Remember(
                    value = '2000', 
                    description = 'This method should be already removed', 
                    format = 'yyyy'
                )   
                class Subject { }
                
                true
            """
        then:
            MultipleCompilationErrorsException e = thrown(MultipleCompilationErrorsException)
            assertMessage(e, 'This method should be already removed @ line 4, column 17.')
    }

    void 'remember wrong date'() {
        when:
            // language=Groovy
            GroovyAssert.assertScript """
                import com.agorapulse.remember.Remember

                @Remember('the milk')   
                class Subject { }
                
                true
            """
        then:
            MultipleCompilationErrorsException e = thrown(MultipleCompilationErrorsException)
            assertMessage(e, 'Unable to parse date \'the milk\' using format \'yyyy-MM-dd\':\njava.text.ParseException: Unparseable date: "the milk" @ line 4, column 17.')
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
