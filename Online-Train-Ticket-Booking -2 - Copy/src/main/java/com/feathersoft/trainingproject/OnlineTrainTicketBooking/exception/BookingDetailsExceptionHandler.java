package com.feathersoft.trainingproject.OnlineTrainTicketBooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;

@ControllerAdvice
public class BookingDetailsExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ErrorDetails errorDetails =new ErrorDetails(LocalDate.now(),ex.getMessage(),request.getDescription(false));
        return  new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(BookingsNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleBookingsNotFoundException(Exception ex, WebRequest request){
        ErrorDetails errorDetails =new ErrorDetails(LocalDate.now(),ex.getMessage(),request.getDescription(false));
        return  new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(BookingIdNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleBookingIdNotFoundException(Exception ex, WebRequest request){
        ErrorDetails errorDetails =new ErrorDetails(LocalDate.now(),ex.getMessage(),request.getDescription(false));
        return  new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(InvalidStatusException.class)
    public final ResponseEntity<ErrorDetails> handleInvalidStatusException(Exception ex, WebRequest request){
        ErrorDetails errorDetails =new ErrorDetails(LocalDate.now(),ex.getMessage(),request.getDescription(false));
        return  new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

}
