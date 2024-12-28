package com.game.social.discovery.authentication.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.game.social.discovery.authentication.DTO.LoginUserDTO;
import com.game.social.discovery.authentication.DTO.LoginUserResponseDTO;
import com.game.social.discovery.authentication.DTO.RegisterResponseDTO;
import com.game.social.discovery.authentication.DTO.RegisterUserDTO;
import com.game.social.discovery.authentication.Model.UserAccount;
import com.game.social.discovery.authentication.Repository.UserAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.security.MessageDigest;



@Service
@Slf4j
public class AuthenticationService {

    @Autowired
    UserAccountRepository userAccountRepository;

    private static final long EXPIRATION_TIME = 3600000;

    @Value("${jwt.secret}")
    private String SECRET_KEY;


    public RegisterResponseDTO registerUser(RegisterUserDTO registerUserDTO) {
        RegisterResponseDTO response = new RegisterResponseDTO();
        Optional<UserAccount> userAccount = userAccountRepository.findByEmailId(registerUserDTO.getEmailId());
        if(userAccount.isPresent()){
            response.setSuccess(0);
            return response;
        }
        String salt = encryptPassword(registerUserDTO);
        if(salt == null){
            response.setSuccess(0);
            response.setMessage("some error occured while trying to encrypt the password");
            return response;
        }
        UserAccount newUserAccount = UserAccount.builder()
                        .emailId(registerUserDTO.getEmailId())
                                .password(registerUserDTO.getPassword())
                                        .username(registerUserDTO.getUsername())
                                                .salt(salt)
                                                        .build();
        userAccountRepository.save(newUserAccount);
        response.setSuccess(1);
        response.setMessage("Registered User Successfully");
        return response;
    }

    private String encryptPassword(RegisterUserDTO registerUserDTO) {
        String password = registerUserDTO.getPassword();
        String salt = AuthenticationService.generateRandomString();
        String passwordWithSalt = password + salt;
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = messageDigest.digest(passwordWithSalt.getBytes());
            registerUserDTO.setPassword(Base64.getEncoder().encodeToString(hashBytes));
        } catch (Exception e) {
            log.info("Error occured while trying to encrypt password: {}",e.getMessage());
            return null;
        }
        return salt;

    }

    public static String generateRandomString() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int LENGTH = 16;
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }
        return stringBuilder.toString();
    }

    public LoginUserResponseDTO loginUser(LoginUserDTO loginUserDTO) {
        LoginUserResponseDTO loginUserResponseDTO = new LoginUserResponseDTO();
        String password = loginUserDTO.getPassword();
        Optional<UserAccount> userAccount = userAccountRepository.findByEmailId(loginUserDTO.getEmailId());
        if(userAccount.isEmpty()){
            loginUserResponseDTO.setSuccess(0);
            loginUserResponseDTO.setMessage("Incorrect EmailId");
            return loginUserResponseDTO;
        }
        String salt = userAccount.get().getSalt();
        String passwordWithSalt = loginUserDTO.getPassword()+salt;
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hashBytes = messageDigest.digest(passwordWithSalt.getBytes());
        String encodeToPassword = Base64.getEncoder().encodeToString(hashBytes);
        if(!userAccount.get().getPassword().equals(encodeToPassword)){
            loginUserResponseDTO.setSuccess(0);
            loginUserResponseDTO.setMessage("Incorrect Password");
            return loginUserResponseDTO;
        }
        loginUserResponseDTO.setSuccess(1);
        loginUserResponseDTO.setMessage("Logged In Successfully");
        loginUserResponseDTO.setToken(generateJwtToken(loginUserDTO.getEmailId()));
        return loginUserResponseDTO;
    }


    public String generateJwtToken(String email) {
        try {
            return JWT.create()
                    .withSubject(email)
                    .withIssuedAt(Date.from(Instant.now()))
                    .withExpiresAt(Date.from(Instant.now().plusMillis(EXPIRATION_TIME)))
                    .withClaim("email", email)  // Optional additional claim
                    .sign(getAlgorithm());
        } catch (JWTCreationException e) {
            log.error("Error creating JWT token: {}", e.getMessage());
            throw new RuntimeException("Failed to create JWT token", e);
        }
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(SECRET_KEY);
    }


    public boolean validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(getAlgorithm())
                    .build();

            DecodedJWT jwt = verifier.verify(token);
            String email = jwt.getSubject();

            return userAccountRepository.findByEmailId(email).isPresent();

        } catch (TokenExpiredException e) {
            log.error("JWT token expired: {}", e.getMessage());
        } catch (SignatureVerificationException e) {
            log.error("JWT signature verification failed: {}", e.getMessage());
        } catch (JWTVerificationException e) {
            log.error("JWT verification failed: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during token validation: {}", e.getMessage());
        }
        return false;
    }
}
