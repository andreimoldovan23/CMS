package raven.iss.data.api.dtos;

import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;

@Data
@ToString
public class PCMemberDTO {

    @Valid
    private ConferenceDTO conf;

    @Valid
    private UserDTO user;

    private Boolean likeToReview = false;

}
