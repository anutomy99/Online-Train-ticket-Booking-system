package com.feathersoft.trainingproject.OnlineTrainTicketBooking.service;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.Payment;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.PaymentStatus;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.User;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.exception.BookingIdNotFoundException;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.exception.InvalidStatusException;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.PaymentRepository;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.UserRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InvoiceService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public byte[] generateInvoice(String email, long transactionId){
        try {
            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document,outputStream);
            document.open();
            document.add(new Paragraph(generateInvoiceContext(email,transactionId)));
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateInvoiceContext(String email,long transactionId) {
        Optional<User> recUSer = userRepository.findByEmail(email);
        User user =recUSer.get();
        Payment paymentDetails = paymentRepository.findByTransactionId(transactionId);
        if(user.getId() !=paymentDetails.getUser().getId()){
            throw new BookingIdNotFoundException("Something went wrong unable to generate invoice");
        }
        if (paymentDetails == null){
            throw new IllegalArgumentException("Invalid transaction id...failed to generate invoice");
        }

        StringBuilder invoiceContext = new StringBuilder();

            invoiceContext.append("_______________________________Invoice Details___________________________\n ");
            invoiceContext.append("Booking Id :  ").append(paymentDetails.getBookingDetails().getBooking_id()).append("\n");
            invoiceContext.append("Transaction Id :  ").append(transactionId).append("\n");
            invoiceContext.append("Transaction Date :  ").append(paymentDetails.getTransactionDateTime()).append("\n");
            invoiceContext.append("Passenger Name       :  ").append(user.getName()).append("\n");
            invoiceContext.append("Passenger Email      :  ").append(user.getEmail()).append("\n");
            invoiceContext.append("Passenger PhoneNo      :  ").append(user.getPhone()).append("\n");
            invoiceContext.append("Train Name :  ").append(paymentDetails.getBookingDetails().getTrain().getTrainName()).append("\n");
            invoiceContext.append("CompartmentDetails :  ").append(paymentDetails.getBookingDetails().getCompartmentDetails().getCompartmentType()).append("\n");
            invoiceContext.append("Number of tickets booked :  ").append(paymentDetails.getBookingDetails().getTicketsBooked()).append("\n");
            double GST = paymentDetails.getTotalAmount()*0.08;
            invoiceContext.append("Ticket charge   :  ").append(paymentDetails.getTotalAmount()).append("\n");
            invoiceContext.append("GST             : ").append(GST).append("\n");
            invoiceContext.append("____________________________\n ");
            double totalAmount = paymentDetails.getBookingDetails().getAmountPayable()+GST;
            invoiceContext.append("Total amount    : ").append(totalAmount);

        return invoiceContext.toString();
    }
}
