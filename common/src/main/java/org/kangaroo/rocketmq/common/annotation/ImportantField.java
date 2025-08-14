package org.kangaroo.rocketmq.common.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD,ElementType.LOCAL_VARIABLE,ElementType.PARAMETER})
public @interface ImportantField {
}
 
