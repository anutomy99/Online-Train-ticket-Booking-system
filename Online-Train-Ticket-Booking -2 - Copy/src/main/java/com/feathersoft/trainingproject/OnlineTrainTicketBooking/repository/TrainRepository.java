package com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.CompartmentDetails;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.Train;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface TrainRepository extends JpaRepository<Train,Integer> {


    Train findByTrainNumber(String trainNumber);

    List<Train> findByDateAndFromLocationAndToLocation(LocalDate date, String fromLocation, String toLocation);

    List<CompartmentDetails> findByCompartmentDetails(Optional<Train> train);
}
