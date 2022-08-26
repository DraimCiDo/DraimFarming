package net.draimcido.draimfarming.helper;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Repeatable(MavenLibraries.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavenLibrary {

    @NotNull
    String groupId();

    @NotNull
    String artifactId();

    @NotNull
    String version();

    @NotNull
    Repository repo() default @Repository(url = "https://repo1.maven.org/maven2");

}
