package net.draimcido.draimfarming.helper;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

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
