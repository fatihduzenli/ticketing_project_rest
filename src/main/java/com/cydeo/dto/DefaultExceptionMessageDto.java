package com.cydeo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DefaultExceptionMessageDto {
//Whenever we want to use exception handling with my annotation, this message will be thrown in the screen
// if we don't provide any message default message will be thrown.
    private String message;
}