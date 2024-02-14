package com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;


@Entity
@Data
@JsonIgnoreProperties({"payment_id","user","bookingDetails"})
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int payment_id;

    private String cardNumber;

    private String cardHolder;

    private double totalAmount;
    private long transactionId;

    private LocalDateTime transactionDateTime;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToOne
    @JoinColumn(name = "bookingId")
    private BookingDetails bookingDetails;

}
