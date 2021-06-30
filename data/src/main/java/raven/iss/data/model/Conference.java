package raven.iss.data.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(callSuper = true, exclude = {"chairs", "authors", "pcMembers", "listeners", "rooms", "sessions", "phases"})
@EqualsAndHashCode(callSuper = false, exclude = {"chairs", "authors", "pcMembers", "listeners", "rooms", "sessions", "phases"})

@Entity
public class Conference extends BaseEntity<Integer> {

    @Singular
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "conf")
    private Set<Chair> chairs = new HashSet<>();

    @Singular
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "conf")
    private Set<Author> authors = new HashSet<>();

    @Singular
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "conf")
    private Set<PCMember> pcMembers = new HashSet<>();

    @Singular
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "conf")
    private Set<Listener> listeners = new HashSet<>();

    @Singular
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "conf")
    private Set<Room> rooms = new HashSet<>();

    @Singular
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "conf")
    private Set<Session> sessions = new HashSet<>();

    @Singular
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "conf")
    private Set<Phase> phases = new HashSet<>();

    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String city;

    @Builder
    private Conference(String name, LocalDateTime startDate, LocalDateTime endDate, String city) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.city = city;
    }

}
