package raven.iss.data.model.security;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import raven.iss.data.model.User;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"user"})

@Entity
public class Profile {

    @Id
    @Column(name = "id")
    private Integer id;

    private String username;
    private String password;

    @Builder.Default
    private Boolean noPass = false;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "USER_ID")
    private User user;

    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "profile_role",
            joinColumns = {@JoinColumn(name = "PROFILE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID")})
    private Set<Authority> authorities;

    @Transient
    public Set<GrantedAuthority> getAuthorities() {
        return this.authorities.stream()
                .map(authority ->
                        new SimpleGrantedAuthority(authority.getPermission()))
                .collect(Collectors.toSet());
    }

    public void addAuthority(Authority authority) {
        authorities.add(authority);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    @Builder.Default
    private Boolean enabled = true;

}
