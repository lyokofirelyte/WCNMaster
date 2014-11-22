package com.github.lyokofirelyte.Spectral.Identifiers;

/**
 * Represents a repeating task specific to patrols. <br />
 * The values don't actually do anything, they're just for reference.
 */
public @interface PatrolTask {
	long delay() default 0L;
	long duration() default 0L;
	boolean autoStart() default false;
}