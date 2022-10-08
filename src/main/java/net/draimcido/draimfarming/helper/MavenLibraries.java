package net.draimcido.draimfarming.helper;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavenLibraries {

    @NotNull
    MavenLibrary[] value() default {};

}
