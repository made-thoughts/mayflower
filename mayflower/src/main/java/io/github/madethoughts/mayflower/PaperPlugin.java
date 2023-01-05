package io.github.madethoughts.mayflower;

import jakarta.inject.Singleton;

import java.lang.annotation.*;

@Singleton
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface PaperPlugin {
}
