package com.github.lyokofirelyte.Divinity.Commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface DivCommand {
	public String[] aliases();
	public String name() default "none";
	public String desc() default "A Divinity/Elysian Command";
	public String help() default "/ely ?";
	public String perm() default "wa.member";
	public boolean player() default false;
	public int max() default 9999;
	public int min() default 0;
}