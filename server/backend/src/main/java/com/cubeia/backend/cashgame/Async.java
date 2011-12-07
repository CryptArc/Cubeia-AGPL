package com.cubeia.backend.cashgame;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marker for service methods that are asynchronous.
 * This is only a documentation-annotation and will not be processed.
 * @author w
 */
@Target({ElementType.METHOD})
public @interface Async {

}
