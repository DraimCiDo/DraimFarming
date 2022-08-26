package net.draimcido.draimfarming.helper;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a maven repository.
 */
@Documented
@Target(ElementType.LOCAL_VARIABLE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Repository {

    /**
     * Gets the base url of the repository.
     *
     * @return the base url of the repository
     */
    String url();

}
