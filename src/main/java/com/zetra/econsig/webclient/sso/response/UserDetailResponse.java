package com.zetra.econsig.webclient.sso.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailResponse {

    private String username;
    private LocalDate userExpirationDate;
    private LocalDate passwordExpirationDate;
    private String emailIdentInternEconsig;

}
