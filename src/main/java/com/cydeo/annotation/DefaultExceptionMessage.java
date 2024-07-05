package com.cydeo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultExceptionMessage {

    String defaultMessage() default "";

    /*
    	Purpose: The DefaultExceptionMessage annotation provides a mechanism to associate default messages with methods.
	•	Components:
	•	@Target restricts its usage to methods.
	•	@Retention makes it available at runtime.
	•	defaultMessage() element holds the message, with a default value if not specified.(it specified in the dto exception class)
	•	Use Case: Typically used for exception handling, logging, or providing consistent default messages in application methods.
	•   If any exception occurs in the method that we passed this annotation on, it will be displayed the message that we determine in the dto class
     */

}