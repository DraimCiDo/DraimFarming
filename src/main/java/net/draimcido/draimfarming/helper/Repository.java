package net.draimcido.draimfarming.helper;

import java.lang.annotation.*;

@Documented
@Target(ElementType.LOCAL_VARIABLE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Repository {

    String url();

}
