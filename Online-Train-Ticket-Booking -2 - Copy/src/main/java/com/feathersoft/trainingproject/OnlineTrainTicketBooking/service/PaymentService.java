package com.feathersoft.trainingproject.OnlineTrainTicketBooking.service;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.*;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.exception.*;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.BookingRepository;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.PaymentRepository;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class PaymentService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<String> makePayment(String email, Payment payment,int bookingId) throws Exception {
        Optional<User> recUser = userRepository.findByEmail(email);
        User user  =recUser.get();
        BookingDetails bookingDetails = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingIdNotFoundException("No such booking id"));
        if (bookingDetails.getBookingStatus() == BookingStatus.CANCELLED){
            return new ResponseEntity<>("Booking is already cancelled you cannot make payment for booking id : "+bookingId,HttpStatus.OK);
        }
        if(bookingDetails.getPaymentStatus() == PaymentStatus.PAID){
            return new ResponseEntity<>("Payment is already done for BookingID : "+bookingId,HttpStatus.OK);
        }
        if(user.getId() !=bookingDetails.getUser().getId()){
           throw new BookingIdNotFoundException("Something went wrong please check the booking Id");
        }else {
            payment.setCardNumber(encrypt(payment.getCardNumber()));
            payment.setPaymentStatus(PaymentStatus.PAID);
            payment.setTotalAmount(bookingDetails.getAmountPayable());
            payment.setTransactionId(Math.abs(new Random().nextLong() % 1_000_000_00L));
            payment.setTransactionDateTime(LocalDateTime.now());
            payment.setUser(user);
            payment.setBookingDetails(bookingDetails);
            bookingDetails.setPaymentStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);
        }

        return  new ResponseEntity<>("Payment successful !!! Transaction id : "+payment.getTransactionId(), HttpStatus.OK);
     }


    public ResponseEntity<List<Payment>> checkAllPayments() {
        List<Payment> paymentList = paymentRepository.findAll();
        if (paymentList.isEmpty()){
            throw  new PaymentsNotFoundException("Payments are not available");
        }
        return new ResponseEntity<>(paymentList,HttpStatus.OK);

    }



    public ResponseEntity<List<Payment>> checkAllPaymentsByUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        List<Payment> paymentList = paymentRepository.findByUserId(user.get().getId());
        if( paymentList.isEmpty()){
            throw  new PaymentsNotFoundException("Payment details are not available for this user");
        }
        return new ResponseEntity<>(paymentList,HttpStatus.OK);


    }

    private static final String SECRET_KEY = "1ab728a880a01de3f17a8838d4a482226510e6aa009fa8e317b4598a679e12a1";

    public static String encrypt(String cardNumber) throws Exception {
        SecretKeySpec secretKey = getSecretKey();
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(String.valueOf(cardNumber).getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static SecretKeySpec getSecretKey() {
        byte[] key = SECRET_KEY.getBytes();
        key = Arrays.copyOf(key, 16);
        return new SecretKeySpec(key, "AES");
    }


}


