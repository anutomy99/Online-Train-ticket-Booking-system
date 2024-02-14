package com.feathersoft.trainingproject.OnlineTrainTicketBooking.controller;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.CompartmentDetails;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.CompartmentType;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.ResponseStructure;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.service.CompartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/compartment")
public class CompartmentController {
    @Autowired
    private CompartmentService compartmentService;
    @PostMapping("/add-compartment")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseStructure<CompartmentDetails>> addCompartmentDetails(@RequestBody CompartmentDetails compartmentDetails, @RequestParam(name = "trainId") int trainId,
                                                                                       @RequestParam(name = "compartmentType") CompartmentType compartmentType){
        return compartmentService.addCompartment(compartmentDetails,trainId,compartmentType);
    }
}
