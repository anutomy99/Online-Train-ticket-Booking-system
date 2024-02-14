package com.feathersoft.trainingproject.OnlineTrainTicketBooking.service;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.AuthRequest;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.ResponseStructure;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.Token;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.exception.UserNameNotFoundException;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.TokenRepository;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.UserRepository;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.User;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.exception.UserNotFoundException;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private TokenRepository tokenRepository;
    public static String uploadDirectory= "D:/MainProject/Online-Train-Ticket-Booking -2 - Copy/images";


    public ResponseEntity<ResponseStructure<User>> registerUser(User user, MultipartFile file) throws IOException {
        ResponseStructure<User> structure = new ResponseStructure<>();
        if(!user.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$")){
            throw new IllegalArgumentException("Password must contain at least one uppercase letter, one lowercase letter, and one digit");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String originalFileName = file.getOriginalFilename();
        Path fileNameAndPath = Paths.get(uploadDirectory,originalFileName);
        if (!(file.getContentType().equalsIgnoreCase("image/jpeg")
                ||file.getContentType().equalsIgnoreCase("image/jpg") ||file.getContentType().equalsIgnoreCase("image/png"))){
            throw new InvalidContentTypeException("Invalid image format. Please upload an image in jpg, jpeg, or png format.");
        }
        Files.write(fileNameAndPath,file.getBytes());
        user.setProfileImage(file.getBytes());
        user.setImageUrl(String.valueOf(fileNameAndPath));
        user =userRepository.save(user);
        structure.setMessage("User Registered Successfully");
        structure.setData(user);
        return new ResponseEntity<>(structure, HttpStatus.CREATED);
    }


    public ResponseEntity<ResponseStructure<User>> updateProfile(String email, User user,MultipartFile file) throws IOException {
        ResponseStructure<User> structure = new ResponseStructure<>();
        if(!user.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$")){
            throw new IllegalArgumentException("Password must contain at least one uppercase letter, one lowercase letter, and one digit");
        }
            Optional<User> existUser = userRepository.findByEmail(email);
            if (existUser.isPresent()) {
                User updateUser = existUser.get();
                if (user.getName() != null) {
                    updateUser.setName(user.getName());

                }
                if (user.getAge() != null) {
                    updateUser.setAge(user.getAge());

                }
                if (user.getGender() != null) {
                    updateUser.setGender(user.getGender());

                }
                if (user.getEmail() != null) {
                    updateUser.setEmail(user.getEmail());

                }
                if (user.getPhone() != null) {
                    updateUser.setPhone(user.getPhone());

                }
                if (user.getPassword() != null) {
                    updateUser.setPassword(passwordEncoder.encode(user.getPassword()));

                }
                if (user.getRole() != null) {
                    updateUser.setRole(user.getRole());

                }
                if (file.isEmpty()) {
                    throw new FileNotFoundException("File not found");
                }
                String originalFileName = file.getOriginalFilename();
                Path fileNameAndPath = Paths.get(uploadDirectory, originalFileName);
                if (!(file.getContentType().equalsIgnoreCase("image/jpeg")
                        || file.getContentType().equalsIgnoreCase("image/jpg") || file.getContentType().equalsIgnoreCase("image/png"))) {
                    throw new InvalidContentTypeException("Invalid image format. Please upload an image in jpg, jpeg, or png format.");
                }
                Files.write(fileNameAndPath, file.getBytes());
                updateUser.setProfileImage(file.getBytes());
                updateUser.setImageUrl(String.valueOf(fileNameAndPath));

                userRepository.save(updateUser);
                revokeAllUserTokens(updateUser);
                structure.setMessage("User details updated Successfully");
                structure.setData(user);

            }

        return new ResponseEntity<>(structure, HttpStatus.ACCEPTED);

    }


    public ResponseEntity<String> changePassword(String email, String oldPassword,String newPassword) {

          Optional<User> recUser = userRepository.findByEmail(email);
          User user = recUser.get();
          if(!passwordEncoder.matches(oldPassword,user.getPassword())){
              return new ResponseEntity<>("Invalid current password",HttpStatus.BAD_REQUEST);
          }
          user.setPassword(passwordEncoder.encode(newPassword));
          userRepository.save(user);
          revokeAllUserTokens(user);
          return new ResponseEntity<>("Password changed successfully",HttpStatus.ACCEPTED);
    }


    public ResponseEntity<String > deleteById(int id) {
            userRepository.findById(id).orElseThrow(()->new UserNotFoundException("User not found !!!"));
            userRepository.deleteById(id);
            return new ResponseEntity<>("User deleted with id : "+id,HttpStatus.OK);
    }
    public ResponseEntity<String> deleteAccount(String email) {
        Optional<User> recUser = userRepository.findByEmail(email);
        User user = recUser.get();
        userRepository.delete(user);
        return new ResponseEntity<>("Account deleted",HttpStatus.OK);

    }


    public User finUserById(int id) {
        return userRepository.findById(id).orElseThrow(()->new UserNotFoundException("User not found !!!"));
    }


    public Optional<User> getMyProfile(String email) {
        return userRepository.findByEmail(email);
    }


    public String authenticateAndGetToken(AuthRequest authRequest) throws AccessDeniedException {
        userRepository.findByEmail(authRequest.getEmail()).orElseThrow(()->new UserNotFoundException("Account details not exist.. please create a new account"));
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
                String jwtToken = jwtService.generateToken(authRequest.getEmail());
                revokeAllUserTokens(userRepository.findByEmail(authRequest.getEmail()).get());
                var token = Token.builder().token(jwtToken).user(userRepository.findByEmail(authRequest.getEmail()).get()).revoked(false).expired(false).build();
                tokenRepository.save(token);
                return jwtToken;

        } catch (Exception e) {
            throw new AccessDeniedException("Access denied!!! Invalid email / password");
        }
    }

    private void revokeAllUserTokens(User user) {
        var validUserToken = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserToken.isEmpty()) {
            return;
        }
        validUserToken.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserToken);
    }


}


