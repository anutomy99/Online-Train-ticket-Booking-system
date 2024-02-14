package com.feathersoft.trainingproject.OnlineTrainTicketBooking.controller;


import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.BookingDetails;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.BookingStatus;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.CompartmentType;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.ResponseStructure;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.UserRepository;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.service.BookingService;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/book-ticket")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ResponseStructure<BookingDetails>> bookTicket(@RequestHeader (name="Authorization") String token, @RequestParam(name = "trainNumber") String trainNumber,
                                                                        @RequestParam(name = "compartmentType") CompartmentType compartmentType, @RequestParam(name = "noOfTickets") int noOfTickets){
        String email=jwtService.extractUsername(token.substring(7));
        return bookingService.bookTickets(email,trainNumber,compartmentType,noOfTickets);
    }

    @GetMapping("/display-all-bookings")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<BookingDetails>> displayAllBooking(){
        return bookingService.displayAllBookings();
    }

    @GetMapping("/display-all-booking-by-train/{trainId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<BookingDetails>> displayAllBookingsByTrain(@PathVariable int trainId){
        return bookingService.displayAllBookingsByTrain(trainId);
    }

    //display booking based on user id
    @GetMapping("/display-all-booking-by-user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<BookingDetails>> displayAllBookingByUser(@RequestHeader (name="Authorization") String token){
        String email=jwtService.extractUsername(token.substring(7));
        return bookingService.displayAllBookingsByUser(email);
    }

    //user and admin can cancel the booking
    @PutMapping("/cancel-booking")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> cancelBooking(@RequestHeader (name="Authorization") String token,@RequestParam(name = "bookingId")int bookingId,@RequestParam(name = "nOfTicketsCancel") int nOfTicketsCancel){
        String email=jwtService.extractUsername(token.substring(7));
        return bookingService.cancelBooking(email,bookingId,nOfTicketsCancel);
    }

//    @DeleteMapping("/cancel-booking-by-bookingId/{bookingId}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<String> cancelBookingById(@PathVariable int bookingId){
//        return bookingService.cancelBookingById(bookingId);
//    }


    @PutMapping("/confirm-cancel-booking-status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseStructure<BookingDetails>> updateBookingStatus(@RequestParam(name = "bookingId")int bookingId,
                                                                                 @RequestParam(name = "newBookingStatus") BookingStatus newBookingStatus){
        return bookingService.updateBookingStatus(bookingId,newBookingStatus);
    }


}
