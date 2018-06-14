# Remember
Say Good Bye to Temporary Hacks

# Introduction

`@Remember` is an annotation which helps you not to forget any temporary solution (aka hacks or quick wins)
you have introduced into your code base. You specify the date in the future when you want to revisit the code, e.g. `@Remember('2018-12-24)`.
After this date the code no longer compiles forcing you to re-evaluate if the code is still required or to find
more permanent solution.

## Full Usage

```groovy
import com.agorapulse.remember.Remember

@Remember(
    value = '2019', 
    description = 'This method should be already removed', 
    format = 'yyyy'
)   
class Subject { }
```

You can modify the format of the date `value` by setting `format` property of the annotation.
You can customize the message being shown by using `description` property. 
