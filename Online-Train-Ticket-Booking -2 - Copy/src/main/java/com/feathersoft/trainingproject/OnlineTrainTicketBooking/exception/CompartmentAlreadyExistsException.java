package com.feathersoft.trainingproject.OnlineTrainTicketBooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.ALREADY_REPORTED)

public class CompartmentAlreadyExistsException extends RuntimeException {
    public CompartmentAlreadyExistsException(String message) {
        super(message);
    }
}
