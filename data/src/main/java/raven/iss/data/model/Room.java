package raven.iss.data.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@ToString(callSuper = true, exclude = "sessions")
@EqualsAndHashCode(callSuper = false, exclude = {"sessions"})

@Entity
public class Room extends BaseEntity<Integer> {

    private String name;
    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "CONFERENCE_ID")
    private Conference conf;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "room")
    @Builder.Default
    private List<Session> sessions = new ArrayList<>();

}
