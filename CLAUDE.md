# Remember Project - Helper Commands and Guidelines

## Build & Test Commands
- Build: `./gradlew build`
- Test all: `./gradlew test`
- Test specific class: `./gradlew test --tests "*RememberSpec"`
- Test specific method: `./gradlew test --tests "*.RememberSpec.specificTestMethod"`

## Linting & Style Commands
- Run all checks: `./gradlew check`
- Run checkstyle: `./gradlew checkstyle`
- Run codenarc: `./gradlew codenarc`

## Code Style Guidelines
- Java/Groovy standard naming conventions (camelCase for variables/methods, PascalCase for classes)
- 4-space indentation, no tabs
- Max line length: 160 characters
- Use explicit type declarations where they improve readability
- Write comprehensive Spock tests for all functionality
- Follow clean code principles with meaningful method and variable names
- Add proper documentation for public APIs

## Usage in Java/Groovy
```java
// Java
@Remember(
    value = "2025-01-01",
    description = "Fix this hack",
    owner = "developer",
    ci = true
)
public class TemporaryClass { }

@DoNotMerge("This is experimental code")
public class ExperimentalClass { }
```

```groovy
// Groovy
@Remember(value = '2025-01-01', description = 'Fix this hack')
class TemporaryClass { }

@DoNotMerge('This is experimental code')
class ExperimentalClass { }
```

## Java Compiler Support Implementation
Currently, the annotations only break compilation in Groovy code. To make them work with Java:

1. Create a Java annotation processor module:
   ```
   /libs/remember-processor/
     remember-processor.gradle
     src/main/java/com/agorapulse/remember/processor/
       RememberProcessor.java
       DoNotMergeProcessor.java
   ```

2. Implement processors using javax.annotation.processing API:
   ```java
   @AutoService(Processor.class)
   public class RememberProcessor extends AbstractProcessor {
       // Implement date checking logic similar to RememberTransformation
       // Use processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, element)
       // to create build errors
   }
   ```

3. Register processors in META-INF/services
4. Add Auto-Service dependency for annotation processor registration

## Project Structure
- Remember is a library for managing temporary code and experiments
- @Remember annotation enforces expiration dates for temporary solutions
- @DoNotMerge annotation prevents merging experimental code into main branches
- Currently only enforced during Groovy compilation