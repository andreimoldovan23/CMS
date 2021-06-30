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
@ToString(callSuper = true, exclude = {"shouldReview", "authors", "reviews", "presentationFile", "paperFile"})
@EqualsAndHashCode(callSuper=false, exclude = {"shouldReview", "authors", "reviews", "presentationFile", "paperFile", "keywords", "topics", "status"})

@Entity
public class Paper extends BaseEntity<Integer> {

    private String paperFile;
    private String presentationFile;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "PaperKeywords", joinColumns = @JoinColumn(name = "PAPER_ID"))
    private Set<String> keywords = new HashSet<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "PaperTopics", joinColumns = @JoinColumn(name = "PAPER_ID"))
    private Set<String> topics = new HashSet<>();

    private String name;

    @Builder.Default
    @ManyToMany(mappedBy = "papers")
    private Set<Author> authors = new HashSet<>();

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "paper")
    private Set<Review> reviews = new HashSet<>();

    @Builder.Default
    @ManyToMany(mappedBy = "toReview")
    private Set<PCMember> shouldReview = new HashSet<>();

    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    private Status status = Status.inReview;

}
