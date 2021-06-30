package raven.iss.data.api.dtos;

import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;

@Data
@ToString
public class ChairDTO {

    @Valid
    private UserDTO user;

    @Valid
    private ConferenceDTO conf;

}
