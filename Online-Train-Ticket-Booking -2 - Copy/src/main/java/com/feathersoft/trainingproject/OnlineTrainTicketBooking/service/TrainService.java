package com.feathersoft.trainingproject.OnlineTrainTicketBooking.service;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.*;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.CompartmentRepository;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.TrainRepository;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.exception.TrainNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class TrainService {
    @Autowired
    private TrainRepository trainRepository;
    @Autowired
    private CompartmentRepository compartmentRepository;

    public ResponseEntity<ResponseStructure<Train>> addTrain(Train train) {
        ResponseStructure<Train> structure = new ResponseStructure<>();
        train =trainRepository.save(train);
        structure.setMessage("Train details added");
        structure.setData(train);
        return new ResponseEntity<ResponseStructure<Train>>(structure,HttpStatus.CREATED);
    }
    public ResponseEntity<List<Train>> searchTrain(LocalDate date,String fromLocation,String toLocation ) {
        List<Train> trainList = trainRepository.findByDateAndFromLocationAndToLocation(date,fromLocation,toLocation);
        if (trainList.isEmpty()){
            throw new TrainNotFoundException("Trains are not  available");
        }
        return new ResponseEntity<>(trainList,HttpStatus.FOUND);
    }

    public ResponseEntity<ResponseStructure<Train>> updateTrainDetails(Train train, int trainId) {
        ResponseStructure<Train> structure = new ResponseStructure<>();
        Train updateTrain = trainRepository.findById(trainId).orElseThrow(()->new TrainNotFoundException("Train is not found with the given credentials !!!!!") );
        if (updateTrain != null) {

            if (train.getTrainName() != null) {
                updateTrain.setTrainName(train.getTrainName());
            }

            if (train.getTrainNumber() != null) {
                updateTrain.setTrainNumber(train.getTrainNumber());
            }
            if (train.getFromLocation() != null) {
                updateTrain.setFromLocation(train.getFromLocation());
            }
            if (train.getToLocation() != null) {
                updateTrain.setToLocation(train.getToLocation());
            }
            if (train.getDate() != null) {
                updateTrain.setDate(train.getDate());
            }
            if (train.getTime() !=null){
                updateTrain.setTime(train.getTime());
            }
        }

        trainRepository.save(updateTrain);
        structure.setMessage("Train details updated successfully");
        structure.setData(updateTrain);
        return new ResponseEntity<>(structure, HttpStatus.ACCEPTED);
    }

    public ResponseEntity<ResponseStructure<CompartmentDetails>> changeTicketPrice(int trainId, CompartmentType compartmentType, double newTicketPrice) {
        ResponseStructure<CompartmentDetails> structure = new ResponseStructure<>();
        Train trainDetails = trainRepository.findById(trainId).orElseThrow(()->new TrainNotFoundException("Train not found"));
        Optional<CompartmentDetails> compartmentToUpdate = trainDetails.getCompartmentDetails().stream()
               .filter(c->c.getCompartmentType().equals(compartmentType))
               .findFirst();
        if(compartmentToUpdate.isPresent()){
            CompartmentDetails compartment = compartmentToUpdate.get();
            if (newTicketPrice !=0.0) {
                compartment.setTicketPrice(newTicketPrice);
            }
            compartmentRepository.save(compartment);
            structure.setMessage("Ticket price changed successfully");
            structure.setData(compartment);
            return new ResponseEntity<>(structure, HttpStatus.ACCEPTED);

        }else {
            throw new IllegalArgumentException("Compartment type "+compartmentType+" not found in this train");
        }
    }

    public ResponseEntity<ResponseStructure<CompartmentDetails>> checkAvailableSeats(int trainId, CompartmentType compartmentType) {
        ResponseStructure<CompartmentDetails> structure = new ResponseStructure<>();
        Train train = trainRepository.findById(trainId).orElseThrow(()->new TrainNotFoundException("Train not found"));

        CompartmentDetails compartment = compartmentRepository.findByCompartmentTypeAndTrain(compartmentType,train);
        if (compartment == null){
            throw new IllegalArgumentException("Invalid compartment details details");
        }
        structure.setMessage("Seat availability for train "+train.getTrainName()+" from "+train.getFromLocation()+" to "+train.getToLocation());
        structure.setData(compartment);
        return new ResponseEntity<>(structure, HttpStatus.FOUND);
    }

      public ResponseEntity<String> deleteById(int trainId) {
            trainRepository.findById(trainId).orElseThrow(()->new TrainNotFoundException("Train not found"));
            trainRepository.deleteById(trainId);
            return new ResponseEntity<>("Train details deleted",HttpStatus.OK);
        }

}
