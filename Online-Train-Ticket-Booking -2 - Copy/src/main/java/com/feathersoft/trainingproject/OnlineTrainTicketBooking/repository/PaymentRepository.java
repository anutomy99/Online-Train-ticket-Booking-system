package com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.BookingDetails;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Integer> {


    Payment findByTransactionId(long transactionId);

    List<Payment> findByUserId(int id);

    Payment findByBookingDetails(BookingDetails bookingDetails);
}
