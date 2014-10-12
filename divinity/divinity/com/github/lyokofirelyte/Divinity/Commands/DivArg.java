package com.github.lyokofirelyte.Divinity.Commands;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface DivArg {
	
	public String[] refs();
	public String perm() default "wa.member";
	public boolean player() default false;
	
}
