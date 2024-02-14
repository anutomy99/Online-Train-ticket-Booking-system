package com.feathersoft.trainingproject.OnlineTrainTicketBooking.controller;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.CompartmentDetails;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.CompartmentType;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.ResponseStructure;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.Train;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.service.TrainService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/train")
public class TrainController {
    @Autowired
    private TrainService trainService;

    @PostMapping("/add-train")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseStructure<Train>> addTrain(@Valid @RequestBody Train train){
        return trainService.addTrain(train);
    }


    @GetMapping("/search-train")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<List<Train>> searchTrain(@RequestParam(name = "date") LocalDate date,@RequestParam(name = "fromLocation")String fromLocation,
                                                   @RequestParam(name = "toLocation")String toLocation){
        return trainService.searchTrain(date,fromLocation,toLocation);
    }

    @PutMapping("/update-train-details/{trainId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseStructure<Train>> updateTrainDetails(@Valid @RequestBody Train train,@PathVariable int trainId){
        return trainService.updateTrainDetails(train,trainId);
    }


    @PutMapping("/change-ticket-price")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseStructure<CompartmentDetails>> changeTicketPrice(@RequestParam int trainId, @RequestParam CompartmentType compartmentType,
                                                                                   @RequestParam double newTicketPrice){
        return trainService.changeTicketPrice(trainId,compartmentType,newTicketPrice);
    }
    @GetMapping("/check-available-seats")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseStructure<CompartmentDetails>> checkAvailableSeats(@RequestParam int trainId, @RequestParam CompartmentType compartmentType){
        return trainService.checkAvailableSeats(trainId,compartmentType);
    }
    @DeleteMapping("/delete-train/{trainId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteTrain( @PathVariable int trainId){
        return trainService.deleteById(trainId);

    }


}
