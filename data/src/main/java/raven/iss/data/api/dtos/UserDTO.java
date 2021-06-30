package raven.iss.data.api.dtos;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@ToString
public class UserDTO {

    private Integer id;

    @Size(min = 3, max = 25)
    @Pattern(regexp = "^[a-zA-Z]+([a-zA-Z0-9]+)*$")
    private String username;

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

    @URL
    private String website;

}
