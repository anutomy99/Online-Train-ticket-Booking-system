package com.feathersoft.trainingproject.OnlineTrainTicketBooking.controller;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.service.InvoiceService;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.service.JwtService;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private JwtService jwtService;
    @GetMapping("/download-invoice/{transactionId}")
    public ResponseEntity<byte[]> downloadInvoice(@RequestHeader(name = "Authorization") String token, @PathVariable long transactionId) {
        String email = jwtService.extractUsername(token.substring(7));
        byte[] invoiceContext = invoiceService.generateInvoice(email, transactionId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        httpHeaders.setContentDispositionFormData("attachment", "invoice.pdf");

        return new ResponseEntity<>(invoiceContext,httpHeaders, HttpStatus.OK);

    }
}
