package com.feathersoft.trainingproject.OnlineTrainTicketBooking.service;


import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.*;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.exception.*;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrainRepository trainRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CompartmentRepository compartmentRepository;

    public ResponseEntity<ResponseStructure<BookingDetails>> bookTickets(String email, String trainNumber, CompartmentType compartmentType, int noOfTickets) {
        ResponseStructure<BookingDetails> structure = new ResponseStructure<>();

        Optional<User> recUSer=userRepository.findByEmail(email);
        User user = recUSer.get();
        Train train = trainRepository.findByTrainNumber(trainNumber);
        if (train == null) {
            throw new TrainNotFoundException("Train is not found with the given credentials !!!!!");
        }
        CompartmentDetails compartmentDetails = compartmentRepository.findByCompartmentTypeAndTrain(compartmentType,train);
        if (compartmentDetails == null) {
            throw new IllegalArgumentException("Invalid compartment details");
        }
        if (compartmentDetails.getAvailableSeats()<noOfTickets){
            throw new InsufficientSeatsException("Not enough seats available  in compartmentDetails "+compartmentType+" for train "+trainNumber);
        }

        BookingDetails bookingDetails = new BookingDetails();
        bookingDetails.setBookingStatus(BookingStatus.PENDING);
            bookingDetails.setPaymentStatus(PaymentStatus.PENDING);
            bookingDetails.setUser(user);
            bookingDetails.setTrain(train);
            bookingDetails.setCompartmentDetails(compartmentDetails);
            bookingDetails.setTicketsBooked(noOfTickets);
            bookingDetails.setAmountPayable(noOfTickets * compartmentDetails.getTicketPrice());
               if(!compartmentType.name().equals("GENERAL")){
                 compartmentDetails.setAvailableSeats(compartmentDetails.getAvailableSeats() - noOfTickets);
               }
            bookingDetails.setCompartmentDetails(compartmentDetails);
            compartmentRepository.save(compartmentDetails);
            bookingRepository.save(bookingDetails);
            structure.setMessage("Booking successful !!! booking id :  " + bookingDetails.getBooking_id());
            structure.setData(bookingDetails);

            return new ResponseEntity<>(structure, HttpStatus.OK);

    }


    public ResponseEntity<List<BookingDetails>> displayAllBookings() {
        List<BookingDetails> bookingDetailstList = bookingRepository.findAll();
        if (bookingDetailstList.isEmpty()){
            throw  new BookingsNotFoundException("Booking details are not available");
        }
        return new ResponseEntity<>(bookingDetailstList,HttpStatus.OK);
    }


    public ResponseEntity<List<BookingDetails>> displayAllBookingsByTrain(int trainId) {
        List<BookingDetails> trainList = bookingRepository.findByTrainId(trainId);
        if (trainList.isEmpty()){
            throw new TrainNotFoundException("No such train exist");
        }
        return new ResponseEntity<>(trainList,HttpStatus.FOUND);
    }


    public ResponseEntity<String> cancelBooking(String email,int bookingId,int noOfTicketsCancel) {
            Optional<User> recUser = userRepository.findByEmail(email);

            BookingDetails bookingDetails = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingIdNotFoundException("No such booking id"));
            CompartmentDetails compartment = bookingDetails.getCompartmentDetails();

            if(recUser.get().getId() != bookingDetails.getUser().getId()){
                return new ResponseEntity<>("Something went wrong please check the booking id ",HttpStatus.BAD_REQUEST);
            }

            if (bookingDetails.getTicketsBooked() < noOfTicketsCancel || bookingDetails.getTicketsBooked() ==0 ) {
                return new ResponseEntity<>("You cannot cancel tickets more than you booked or all booked tickets are cancelled", HttpStatus.BAD_REQUEST);
            }
            Payment paymentDetails = paymentRepository.findByBookingDetails(bookingDetails);
            double amountPayable = (bookingDetails.getTicketsBooked()-noOfTicketsCancel)*(compartment.getTicketPrice());
            bookingDetails.setAmountPayable(amountPayable);
            bookingDetails.setTicketsBooked(bookingDetails.getTicketsBooked() - noOfTicketsCancel);
            if (paymentDetails !=null) {
                paymentDetails.setTotalAmount(amountPayable);
            }
            if(!compartment.getCompartmentType().name().equals("GENERAL")) {
                compartment.setAvailableSeats(compartment.getAvailableSeats() + noOfTicketsCancel);
                compartmentRepository.save(compartment);
            }
            if (bookingDetails.getPaymentStatus() == PaymentStatus.PAID){
                   bookingDetails.setPaymentStatus(PaymentStatus.REFUNDED);
                   paymentDetails.setPaymentStatus(PaymentStatus.REFUNDED);
                   paymentRepository.save(paymentDetails);
            }
            if(bookingDetails.getTicketsBooked() == 0){
                bookingDetails.setBookingStatus(BookingStatus.CANCELLED);
            }
           bookingRepository.save(bookingDetails);
           return new ResponseEntity<>("Booking cancelled refund amount  will credited in your account",HttpStatus.ACCEPTED);
    }

    public ResponseEntity<List<BookingDetails>> displayAllBookingsByUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        List<BookingDetails> userBookingList = bookingRepository.findByUserId(user.get().getId());
        if (userBookingList.isEmpty()) {
            throw new PaymentsNotFoundException("Booking details are not available for this user");
        }
        return new ResponseEntity<>(userBookingList, HttpStatus.OK);
    }



//    public ResponseEntity<String> cancelBookingById(int bookingId) {
//        BookingDetails bookingDetails = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingIdNotFoundException("No such booking id"));
//        CompartmentDetails compartment = bookingDetails.getCompartmentDetails();
//
//        if(!compartment.getCompartmentType().name().equals("GENERAL")) {
//            compartment.setAvailableSeats(compartment.getAvailableSeats() + bookingDetails.getTicketsBooked());
//        }
//        Payment payment = paymentRepository.findByBookingDetails(Optional.of(bookingDetails));
//        if (payment.getPaymentStatus().equals(PaymentStatus.PAID))
//        {
//            return new ResponseEntity<>("Refund initiated,credited in your account within 3 working days",HttpStatus.OK);
//        }
//            paymentRepository.delete(payment);
//            bookingRepository.deleteById(bookingId);
//            compartmentRepository.save(compartment);
//
//        return new ResponseEntity<>("Booking cancelled ",HttpStatus.ACCEPTED);
//    }


    public ResponseEntity<ResponseStructure<BookingDetails>> updateBookingStatus(int bookingId, BookingStatus newBookingStatus) {
        ResponseStructure<BookingDetails> structure = new ResponseStructure<>();
        BookingDetails bookingDetails = bookingRepository.findById(bookingId).orElseThrow(()->new BookingIdNotFoundException("Booking id : "+bookingId+" not found"));

        Payment payment = paymentRepository.findByBookingDetails(bookingDetails);

        BookingDetails bookingDetail;
        CompartmentDetails compartment = bookingDetails.getCompartmentDetails();

        switch (newBookingStatus) {
          case CONFIRMED:
              if (bookingDetails.getBookingStatus() == BookingStatus.PENDING) {
                  bookingDetails.setBookingStatus(newBookingStatus);
                  bookingDetail= bookingRepository.save(bookingDetails);
              }else {
                  throw new InvalidStatusException("Booking id : "+bookingId+" in "+ bookingDetails.getBookingStatus()+" state");
              }
              break;

          case CANCELLED:

              if (bookingDetails.getBookingStatus() == BookingStatus.PENDING) {
                  if (bookingDetails.getPaymentStatus() != PaymentStatus.PAID){
                      if(!compartment.getCompartmentType().name().equals("GENERAL")) {
                          compartment.setAvailableSeats(compartment.getAvailableSeats() + bookingDetails.getTicketsBooked());
                      }
                      bookingDetails.setPaymentStatus(PaymentStatus.FAILED);
                      bookingDetails.setBookingStatus(newBookingStatus);
                      bookingDetail= bookingRepository.save(bookingDetails);


                  }else {
                      if(!compartment.getCompartmentType().name().equals("GENERAL")) {
                          compartment.setAvailableSeats(compartment.getAvailableSeats() + bookingDetails.getTicketsBooked());
                      }
                      payment.setPaymentStatus(PaymentStatus.REFUNDED);
                      bookingDetails.setPaymentStatus(PaymentStatus.REFUNDED);
                      bookingDetails.setBookingStatus(newBookingStatus);
                      bookingDetail= bookingRepository.save(bookingDetails);
                  }
              }else {
                  throw new InvalidStatusException("Booking id : "+bookingId+" in "+ bookingDetails.getBookingStatus()+" state you can't cancel the booking again" );
              }
              break;

              default:
                   throw new InvalidStatusException("You are trying to use inappropriate booking status");
      }
        structure.setMessage("Booking status updated successfully");
        structure.setData(bookingDetail);
      return new ResponseEntity<>(structure, HttpStatus.ACCEPTED);
    }
}