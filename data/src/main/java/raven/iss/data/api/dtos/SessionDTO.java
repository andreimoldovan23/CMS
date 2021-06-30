package raven.iss.data.api.dtos;

import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@ToString
public class SessionDTO {

    private Integer id;

    @Size(min = 3, max = 25)
    @Pattern(regexp = "^([a-zA-Z])+([ \\-'][a-zA-Z]+)*$")
    private String name;

    @Size(min = 3, max = 25)
    @Pattern(regexp = "^([a-zA-Z])+([ \\-'][a-zA-Z]+)*$")
    private String topic;

    @Valid
    private RoomDTO room;

    @Valid
    private ConferenceDTO conf;

    @Valid
    private ChairDTO chair;

}
