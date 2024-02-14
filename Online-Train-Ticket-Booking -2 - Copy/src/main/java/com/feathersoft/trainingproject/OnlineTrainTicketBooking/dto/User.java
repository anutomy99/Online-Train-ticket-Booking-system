package com.feathersoft.trainingproject.OnlineTrainTicketBooking.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Entity
@Data
@JsonIgnoreProperties({"id","bookingDetails"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @NotBlank
    @NotNull
    @Column(name = "Name")
    private String name;

  
    @Column(name = "Age")
    @NotNull
    private Integer age;

    @NotBlank
    @Column(name = "Gender")
    private String gender;

    @NotBlank
    @NotEmpty
    @Email
    @Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$",message = "Enter a valid email address")
    @Column(name = "Email",unique = true)
    private String email;


    @NotBlank(message = "mobile number is required")
    @Size(min = 10, max = 10)
    private String phone;

    @JsonIgnore
    private String password;

    @NotBlank
    @JsonIgnore
    private String role;

    @Column(name = "Image_URL")
    private String imageUrl;

    @Column(name = "Image",length = 16777216)
    private byte[] profileImage;


    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<BookingDetails> bookingDetails;

    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Token> tokens;

}
