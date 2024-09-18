package com.ticketingapp.shared.exeptions;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.UUID;

public class ValueNotFoundForIdException extends AppSpecificException{
     public ValueNotFoundForIdException(String requestedValue, UUID id){
         super("Could not find " + requestedValue + " for this id", ValueNotFoundForIdException.class.getSimpleName(), HttpStatus.NOT_FOUND, id.toString(), new ArrayList<>());
     }
}
