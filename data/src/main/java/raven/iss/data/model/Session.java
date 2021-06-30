package raven.iss.data.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@ToString(callSuper = true, exclude = {"speakers", "chair", "watchers"})
@EqualsAndHashCode(callSuper = false, exclude = {"room", "speakers", "chair", "watchers"})

@Entity
public class Session extends BaseEntity<Integer> {

    private String name;
    private String topic;

    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "CONFERENCE_ID")
    private Conference conf;

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "SessionAuthors",
            joinColumns = @JoinColumn(name = "SESSION_ID"), inverseJoinColumns = @JoinColumn(name = "AUTHOR_ID"))
    private Set<Author> speakers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "SESSION_CHAIR_ID")
    private Chair chair;

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "SessionListeners",
            joinColumns = @JoinColumn(name = "SESSION_ID"), inverseJoinColumns = @JoinColumn(name = "LISTENER_ID"))
    private Set<Listener> watchers = new HashSet<>();

}
