package com.feathersoft.trainingproject.OnlineTrainTicketBooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)

public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException(String message) {

        super(message);
    }
}
