package psam.portfolio.sunder.english.web.user.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import psam.portfolio.sunder.english.global.jsonformat.KoreanDateTime;
import psam.portfolio.sunder.english.web.user.enumeration.RoleName;
import psam.portfolio.sunder.english.web.user.model.entity.UserRole;

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
