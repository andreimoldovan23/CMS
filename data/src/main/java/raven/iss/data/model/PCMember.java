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
@ToString(callSuper = true, exclude = {"toReview"})
@EqualsAndHashCode(callSuper = false, exclude = {"toReview"})

@Entity
public class PCMember extends BaseEntity<Integer> {

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "MembersReviewPapers",
            joinColumns = @JoinColumn(name = "PCMEMBER_ID"),
            inverseJoinColumns = @JoinColumn(name = "PAPER_ID"))
    private Set<Paper> toReview = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "CONFERENCE_ID")
    private Conference conf;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Builder.Default
    private Boolean likeToReview = false;

}