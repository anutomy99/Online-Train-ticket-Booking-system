package com.feathersoft.trainingproject.OnlineTrainTicketBooking.service;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.CompartmentDetails;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.CompartmentType;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.ResponseStructure;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.Train;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.exception.CompartmentAlreadyExistsException;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.exception.TrainNotFoundException;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.CompartmentRepository;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CompartmentService {
    @Autowired
    private CompartmentRepository compartmentRepository;
    @Autowired
    private TrainRepository trainRepository;
    public ResponseEntity<ResponseStructure<CompartmentDetails>> addCompartment(CompartmentDetails compartment, int trainId, CompartmentType compartmentType) {
        ResponseStructure<CompartmentDetails> structure = new ResponseStructure<>();
        Train train = trainRepository.findById(trainId).orElseThrow(() -> new TrainNotFoundException("Train wit ID : "+trainId+" not found"));
        if (train.getCompartmentDetails().stream()
                .anyMatch(c->c.getCompartmentType().equals(compartmentType))){

            throw new CompartmentAlreadyExistsException("CompartmentDetails of "+compartmentType+" already exists for train "+train.getTrainNumber());
        }
        CompartmentDetails compartmentDetails =  new CompartmentDetails();
        compartmentDetails.setCompartmentType(compartmentType);
        compartmentDetails.setAvailableSeats(compartment.getAvailableSeats());
        compartmentDetails.setTicketPrice(compartment.getTicketPrice());
        compartmentDetails.setTrain(train);
        trainRepository.save(train);
        compartmentRepository.save(compartmentDetails);
        structure.setMessage("Compartment details details added");
        structure.setData(compartmentDetails);

        return new ResponseEntity<>(structure, HttpStatus.CREATED);
    }
}
