package psam.portfolio.sunder.english.web.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.web.user.enumeration.RoleName;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "roles")
@Entity
public class Role {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private RoleName name;

    private Role(RoleName name) {
        this.name = name;
    }

    public static Role of(RoleName name) {
        return new Role(name);
    }
}