package raven.iss.data.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder

@Entity
@Table(indexes = {
        @Index(name = "phaseNameIndex", columnList = "name, CONFERENCE_ID", unique = true)
})
public class Phase extends BaseEntity<Integer> {

    private LocalDateTime deadline;
    private String name;

    @Builder.Default
    private Boolean isActive = false;

    @ManyToOne
    @JoinColumn(name = "CONFERENCE_ID")
    private Conference conf;

}
