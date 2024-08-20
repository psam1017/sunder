package psam.portfolio.sunder.english.domain.user.model.response;

import lombok.Getter;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;

import java.util.List;

@Getter
public class LoginResult {

    private final String refreshToken;
    private final String accessToken;
    private final boolean passwordChangeRequired;
    private final String userId;
    private final String academyId;
    private final List<RoleName> roles;
    private final UserStatus status;

    public LoginResult(String accessToken, String refreshToken, boolean passwordChangeRequired, String userId, String academyId, List<RoleName> roles, UserStatus status) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.passwordChangeRequired = passwordChangeRequired;
        this.userId = userId;
        this.academyId = academyId;
        this.roles = roles;
        this.status = status;
    }
}
