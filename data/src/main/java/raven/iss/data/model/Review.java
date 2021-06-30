package raven.iss.data.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=false, exclude = {"paper"})

@Entity
public class Review extends BaseEntity<Integer> {

    @Enumerated(value = EnumType.STRING)
    private Grade grade;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User reviewer;

    @ManyToOne
    @JoinColumn(name = "PAPER_ID")
    private Paper paper;

    private String suggestion;

}
