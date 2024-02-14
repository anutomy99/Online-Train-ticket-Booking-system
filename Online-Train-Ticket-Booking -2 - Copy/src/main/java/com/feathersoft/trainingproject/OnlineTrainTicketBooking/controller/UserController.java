package com.feathersoft.trainingproject.OnlineTrainTicketBooking.controller;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.AuthRequest;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.ResponseStructure;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.User;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.exception.UserNameNotFoundException;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.UserRepository;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.service.JwtService;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/register-user")
    public ResponseEntity<ResponseStructure<User>> registerUser( @ModelAttribute User user, @RequestParam("image") MultipartFile file ) throws IOException {
        return userService.registerUser(user,file);
    }

    @PutMapping("/manage-profile")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<ResponseStructure<User>> updateProfile( @RequestHeader (name="Authorization") String token,@Valid @ModelAttribute User user,@RequestParam("image") MultipartFile file) throws IOException {
        String email=jwtService.extractUsername(token.substring(7));
        return userService.updateProfile(email,user,file);
    }

    @GetMapping("get-user/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public User findUserById(@PathVariable int id){
        return userService.finUserById(id);
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<String> changePassword (@RequestHeader (name="Authorization") String token,@RequestParam(name = "oldPassword") String oldPassword,
                                                  @RequestParam(name = "newPassword")String newPassword){
        String email=jwtService.extractUsername(token.substring(7));
        return userService.changePassword(email,oldPassword,newPassword);
    }

    @DeleteMapping("/delete-user/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUserById( @PathVariable int id){
        return userService.deleteById(id);

    }
    @DeleteMapping("/delete-account")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<String> deleteAccount(@RequestHeader (name="Authorization") String token){
        String email=jwtService.extractUsername(token.substring(7));
        return userService.deleteAccount(email);

    }
    @GetMapping("/my-info")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    public Optional<User> getMyProfile(@RequestHeader (name="Authorization") String token){
        String email=jwtService.extractUsername(token.substring(7));
        return userService.getMyProfile(email);
    }



    @GetMapping("/downloadProfileImage")
    public ResponseEntity<?> getProfile(@RequestHeader (name="Authorization") String token) throws IOException {
        String email=jwtService.extractUsername(token.substring(7));
        Optional<User> user = userRepository.findByEmail(email);
        byte[] image=Files.readAllBytes(Path.of(user.get().getImageUrl()));
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

    @PostMapping("/login")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) throws AccessDeniedException {
        return userService.authenticateAndGetToken(authRequest);

    }

}
