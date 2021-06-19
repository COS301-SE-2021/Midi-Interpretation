package com.noxception.midisense.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DevelopmentNote {
    String taskName() default "Miscellaneous";
    Developers[] developers() default {};
    String lastModified() default "Never";
    WorkState status() default WorkState.NOT_STARTED;
    String comments() default "";
    enum Developers{ ADRIAN, CLAUDIO, REA, HENDRO, MBUSO }
    enum WorkState{ NOT_STARTED, IN_PROGRESS, PENDING_REVIEW, COMPLETED }
}


