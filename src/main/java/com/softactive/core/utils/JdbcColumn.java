package com.softactive.core.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JdbcColumn {
	String field();

	int keyValue() default 0;

	String joinField() default "";

	int order() default 1000;

	public static int KEY = 1;
	public static int VALUE = 2;

}
