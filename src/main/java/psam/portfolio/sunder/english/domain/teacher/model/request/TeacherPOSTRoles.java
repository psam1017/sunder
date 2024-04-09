package psam.portfolio.sunder.english.domain.teacher.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.global.validator.EnumPattern;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherPOSTRoles {

    @NotEmpty
    @UniqueElements
    private Set<@EnumPattern(regexp = "^(ROLE_DIRECTOR|ROLE_TEACHER)$") RoleName> roles;
}
