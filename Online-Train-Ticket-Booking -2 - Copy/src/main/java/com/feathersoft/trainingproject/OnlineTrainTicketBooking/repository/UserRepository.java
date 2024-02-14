package com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByEmail(String email);

}
