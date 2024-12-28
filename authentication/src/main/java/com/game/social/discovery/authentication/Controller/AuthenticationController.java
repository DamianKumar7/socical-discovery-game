package com.game.social.discovery.authentication.Controller;

import com.game.social.discovery.authentication.DTO.LoginUserDTO;
import com.game.social.discovery.authentication.DTO.LoginUserResponseDTO;
import com.game.social.discovery.authentication.DTO.RegisterResponseDTO;
import com.game.social.discovery.authentication.DTO.RegisterUserDTO;
import com.game.social.discovery.authentication.Service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;


    //NOTE-> WHEN CREATING A USER, CLIENT WILL FIRST SEND A REQUEST TO REGISTER, THEN IT WILL SEND A REQUEST TO LOGIN AND THEN GET THE JWT TOKEN AND CREATE THE USER IN USER_MANAGEMENT

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserDTO registerUserDTO){
        RegisterResponseDTO response = authenticationService.registerUser(registerUserDTO);
        if (response.getSuccess()!=null &&  response.getSuccess() == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response); // Successful registration
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Registration failed
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginUserDTO loginUserDTO){
        LoginUserResponseDTO response = authenticationService.loginUser(loginUserDTO);
        if (response.getSuccess()!=null &&  response.getSuccess() == 1) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(HttpServletRequest httpServletRequest){
        String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        boolean isValid = authenticationService.validateToken(token);
        if (!isValid) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        return ResponseEntity.ok("Token is valid");
    }
}
