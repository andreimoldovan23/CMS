package raven.iss.data.api.dtos;

import lombok.Data;
import lombok.ToString;
import raven.iss.data.model.Status;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@ToString
public class PaperDTO {

    private Integer id;

    @Size(min = 3, max = 25)
    @Pattern(regexp = "^([a-zA-Z])+([ \\-'][a-zA-Z]+)*$")
    private String name;

    private Status status = Status.inReview;

    private Set<String> topics = new HashSet<>();

    private Set<String> keywords = new HashSet<>();

}
