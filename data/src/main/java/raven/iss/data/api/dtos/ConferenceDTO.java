package raven.iss.data.api.dtos;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@ToString
public class ConferenceDTO {

    private Integer id;

    @Size(min = 3, max = 25)
    @Pattern(regexp = "^([a-zA-Z])+([ \\-'][a-zA-Z]+)*$")
    private String name;

    private String startDate;
    private String endDate;

    @Size(min = 3, max = 25)
    @Pattern(regexp = "^([a-zA-Z])+([ \\-'][a-zA-Z]+)*$")
    private String city;

}
