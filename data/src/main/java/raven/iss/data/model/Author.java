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
@ToString(callSuper = true, exclude = {"papers", "sessionSpeakers"})
@EqualsAndHashCode(callSuper = false, exclude = {"papers", "sessionSpeakers"})

@Entity
public class Author extends BaseEntity<Integer> {

    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "AuthorsPapers",
        joinColumns = @JoinColumn(name = "AUTHOR_ID"),
            inverseJoinColumns = @JoinColumn(name = "PAPER_ID"))
    private Set<Paper> papers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Builder.Default
    @ManyToMany(mappedBy = "speakers")
    private Set<Session> sessionSpeakers = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "CONFERENCE_ID")
    private Conference conf;

}
