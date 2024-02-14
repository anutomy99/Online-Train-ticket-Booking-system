package com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@JsonIgnoreProperties({"booking_id","user","train","payment","compartmentDetails"})
public class BookingDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Booking_Id")
    private int booking_id;

    @Column(name = "Tickets Booked")
    private int ticketsBooked;

    @Column(name = "Amount Payable")
    private double amountPayable;

    @Enumerated(EnumType.STRING)
    @Column(name = "Booking Status")
    private BookingStatus bookingStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "Payment Status")
    private PaymentStatus paymentStatus;


    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "trainId")
    private Train train;

    @ManyToOne
    @JoinColumn(name = "compartment_id")
    private CompartmentDetails compartmentDetails;

    @OneToOne(mappedBy = "bookingDetails", cascade = CascadeType.ALL)
    private Payment payment;
}
