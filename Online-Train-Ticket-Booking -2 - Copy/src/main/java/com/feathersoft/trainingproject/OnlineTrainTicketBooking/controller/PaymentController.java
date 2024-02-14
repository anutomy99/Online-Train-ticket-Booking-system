package com.feathersoft.trainingproject.OnlineTrainTicketBooking.controller;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.Payment;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.service.JwtService;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/payments")

public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    @Autowired
    private JwtService jwtService;

    @PostMapping("/make-payment/{bookingId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> makePayment(@RequestHeader(name = "Authorization") String token, @RequestBody Payment payment, @PathVariable int bookingId) throws Exception {
        String email = jwtService.extractUsername(token.substring(7));
        return paymentService.makePayment(email, payment, bookingId);
    }

    @GetMapping("/check-all-payments")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Payment>> checkAllPayments() {
        return paymentService.checkAllPayments();

    }

    @GetMapping("/check-payments-by-user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<Payment>> checkAllPaymentsByUser(@RequestHeader(name = "Authorization") String token) {
        String email = jwtService.extractUsername(token.substring(7));
        return paymentService.checkAllPaymentsByUser(email);
    }


}