package com.game.social.discovery.authentication.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserResponseDTO {
    String message;
    Integer success;
    String token;
}
