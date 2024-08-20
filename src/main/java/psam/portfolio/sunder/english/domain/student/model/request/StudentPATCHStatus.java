package psam.portfolio.sunder.english.domain.student.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.global.validator.EnumPattern;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StudentPATCHStatus {

    @NotNull
    @EnumPattern(regexp = "^(PENDING|ACTIVE|WITHDRAWN)$")
    private UserStatus status;
}
