package com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto;

import lombok.Data;

@Data
public class ResponseStructure<T> {
    private String message;
    private T data;
}
