package com.ecomerce.sbecom.security.response;

import lombok.*;

import java.util.List;

@NoArgsConstructor
public class UserInfoResponse {
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private String jwtToken;

    @Setter
    @Getter
    private String username;

    @Setter
    @Getter
    private List<String> roles;

    public UserInfoResponse(Long id,String jwtToken, String username, List<String> roles) {
        this.id = id;
        this.jwtToken = jwtToken;
        this.username = username;
        this.roles = roles;
    }

    public UserInfoResponse(Long id, String username, List<String> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }
}
