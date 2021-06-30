package raven.iss.data.model;

import lombok.*;
import raven.iss.data.model.security.Profile;

import javax.persistence.*;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
@ToString(callSuper = true, exclude = {"profile"})
@EqualsAndHashCode(callSuper = false, exclude = {"profile"})

@Entity
@Table(indexes = {
        @Index(name = "usernameIndex", columnList = "username", unique = true)
})
public class User extends BaseEntity<Integer> {
    private String username;
    private String name;
    private String job;
    private String mail;
    private String website;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Profile profile;

    private String phoneNumber;
}
