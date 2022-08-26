package net.draimcido.draimfarming.helper;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate a required library for a class.
 */
@Documented
@Repeatable(MavenLibraries.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavenLibrary {

    /**
     * The group id of the library
     *
     * @return the group id of the library
     */
    @NotNull
    String groupId();

    /**
     * The artifact id of the library
     *
     * @return the artifact id of the library
     */
    @NotNull
    String artifactId();

    /**
     * The version of the library
     *
     * @return the version of the library
     */
    @NotNull
    String version();

    /**
     * The repo where the library can be obtained from
     *
     * @return the repo where the library can be obtained from
     */
    @NotNull
    Repository repo() default @Repository(url = "https://repo1.maven.org/maven2");

}
