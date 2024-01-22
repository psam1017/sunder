package psam.portfolio.sunder.english.web.role.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.web.user.entity.User;
import psam.portfolio.sunder.english.web.role.enumeration.UserRole;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
public class Role {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private UserRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


}
