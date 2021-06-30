package raven.iss.data.api.dtos;

import lombok.Data;
import lombok.ToString;
import raven.iss.data.model.Grade;

import javax.validation.Valid;

@Data
@ToString
public class ReviewDTO {

    private Integer id;

    private Grade grade;

    @Valid
    private UserDTO reviewer;

    @Valid
    private PaperDTO paper;

    private String suggestion;
}
