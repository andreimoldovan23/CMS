package raven.iss.data.model.security;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    private String permission;

    @ManyToMany(cascade = CascadeType.MERGE, mappedBy = "authorities")
    private Set<Profile> profiles;

}
