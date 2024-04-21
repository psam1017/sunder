package psam.portfolio.sunder.english.domain.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import psam.portfolio.sunder.english.global.jsonformat.KoreanDateTime;
import psam.portfolio.sunder.english.domain.user.model.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;

import java.time.LocalDateTime;

import static lombok.AccessLevel.*;

@AllArgsConstructor(access = PRIVATE)
@Getter
public class UserRoleResponse {

    private Long id;
    private RoleName name;
    @KoreanDateTime
    private LocalDateTime assignedDateTime;

    public static UserRoleResponse from(UserRole userRole) {
        return new UserRoleResponse(
                userRole.getId(),
                userRole.getRoleName(),
                userRole.getAssignedDateTime()
        );
    }
}
