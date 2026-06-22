package com.xarhabia.BankApp.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auditable {
    String action(); // ej: "CREAR_USUARIO", "LOGIN", "TRANSFERENCIA"
}
