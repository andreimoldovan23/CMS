package raven.iss.data.api.dtos;

import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@ToString
public class PhaseDTO {

    private Integer id;

    @Valid
    private ConferenceDTO conf;

    @Size(min = 3, max = 25)
    @Pattern(regexp = "^((?:REGISTRATION|SESSION_REGISTRATION|BIDDING|REVIEWING|SUBMITTING))$")
    private String name;

    private String deadlineString;

    private Boolean isActive = false;

}
