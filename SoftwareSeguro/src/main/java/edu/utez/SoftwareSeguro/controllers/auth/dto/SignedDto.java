package edu.utez.SoftwareSeguro.controllers.auth.dto;

import edu.utez.SoftwareSeguro.models.UserModel;
import lombok.*;

import java.util.List;

@Value
public class SignedDto {
    String token;
    String tokenType;
    UserModel user;

    public SignedDto(String token, String tokenType, UserModel user) {
        this.token = token;
        this.tokenType = tokenType;
        this.user = user;
    }

}
