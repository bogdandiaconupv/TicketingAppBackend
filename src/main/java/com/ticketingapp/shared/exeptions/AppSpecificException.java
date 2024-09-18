package com.ticketingapp.shared.exeptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public abstract class AppSpecificException extends RuntimeException {
    private final HttpStatus errorCode;
    private final String value;
    private final String name;
    private final List<AppSpecificException> errors;

    public AppSpecificException(String message, String name,HttpStatus errorCode, String value,  List<AppSpecificException> errors){
        super(message);
        this.errorCode = errorCode;
        this.value = value;
        this.name = name;
        this.errors = errors;
    }

}
