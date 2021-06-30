package raven.iss.data.api.dtos;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@ToString
public class LoginDTO {

    private Integer id;

    @NotNull
    @Size(min = 3, max = 25)
    @Pattern(regexp = "^[a-zA-Z]+([a-zA-Z0-9]+)*$")
    private String username;

    @NotNull
    @Size(min = 8, max = 15)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[_#@$!%*?&])[A-Za-z\\d#_@$!%*?&]{8,}$")
    private String password;

    @Size(min = 3, max = 25)
    @Pattern(regexp = "^([a-zA-Z])+([ \\-'][a-zA-Z]+)*$")
    private String name;

    @Size(min = 3, max = 25)
    @Pattern(regexp = "^([a-zA-Z])+([ \\-'][a-zA-Z]+)*$")
    private String job;

    @Email
    private String mail;

    @Pattern(regexp = "^(\\+[1-9]{1,2})?0[1-9][0-9]{8,}$")
    private String phoneNumber;

}
