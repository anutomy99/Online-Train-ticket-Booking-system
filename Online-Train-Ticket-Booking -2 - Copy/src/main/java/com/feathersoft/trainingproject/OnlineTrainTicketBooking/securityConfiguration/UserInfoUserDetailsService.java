package com.feathersoft.trainingproject.OnlineTrainTicketBooking.securityConfiguration;

import com.feathersoft.trainingproject.OnlineTrainTicketBooking.exception.UserNameNotFoundException;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.repository.UserRepository;
import com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserInfoUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userInfo= userRepository.findByEmail(email);
        return userInfo.map(UserInfoDetails::new)
                .orElseThrow(()->new UserNameNotFoundException("User not found "+email));

    }
}
