package com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.CompartmentDetails;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.CompartmentType;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.Train;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompartmentRepository extends JpaRepository<CompartmentDetails,Integer> {
    CompartmentDetails findByCompartmentTypeAndTrain(CompartmentType compartmentType, Train train);


//    CompartmentDetails findByCompartmentTypeAndTrain(int trainId, CompartmentType compartmentType);
}
