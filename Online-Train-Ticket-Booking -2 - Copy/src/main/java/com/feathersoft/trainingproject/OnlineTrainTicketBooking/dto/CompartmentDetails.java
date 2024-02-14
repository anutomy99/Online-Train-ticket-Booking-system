package com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@JsonIgnoreProperties({"id","train","bookingDetails"})
public class CompartmentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private CompartmentType compartmentType;

    private  int availableSeats;

    private double ticketPrice;

    @ManyToOne
    @JoinColumn(name = "train_id")
    private Train train;

    @OneToMany(mappedBy = "compartmentDetails", cascade = CascadeType.ALL)
    private List<BookingDetails> bookingDetails;

}
