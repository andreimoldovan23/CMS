package raven.iss.data.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@ToString(callSuper = true, exclude = {"attendingSections"})
@EqualsAndHashCode(callSuper = false, exclude = {"attendingSections"})

@Entity
public class Listener extends BaseEntity<Integer> {

    @Builder.Default
    @ManyToMany(mappedBy = "watchers")
    private Set<Session> attendingSections = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "CONFERENCE_ID")
    private Conference conf;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

}
