package vn.baymax.fjob.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ReqLoginDTO {
    @NotBlank(message = "username is not empty")
    private String username;
    @NotBlank(message = "password is not empty")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
