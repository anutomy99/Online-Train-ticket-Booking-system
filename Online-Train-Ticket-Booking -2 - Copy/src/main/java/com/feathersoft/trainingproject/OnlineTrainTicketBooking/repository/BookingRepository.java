package com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.BookingDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingDetails,Integer> {
    List<BookingDetails> findByTrainId(int trainId);

    List<BookingDetails> findByUserId(int userId);

}
