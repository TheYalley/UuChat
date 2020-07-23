package com.rabbitown.yachat.chat;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to mark classes as a function.
 * 
 * @author Yoooooory
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageHandler {
    FunctionPriority priority() default FunctionPriority.NORMAL;
}