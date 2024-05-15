package psam.portfolio.sunder.english.domain.user.model.response;

import lombok.Getter;
import psam.portfolio.sunder.english.domain.user.model.enumeration.RoleName;

import java.util.List;

@Getter
public class LoginResult {

    private final String refreshToken;
    private final String accessToken;
    private final boolean passwordChangeRequired;
    private final String userId;
    private final List<RoleName> roleNames;

    public LoginResult(String accessToken, String refreshToken, boolean passwordChangeRequired, String userId, List<RoleName> roleNames) {
        this.accessToken = "Bearer " + accessToken;
        this.refreshToken = "Bearer " + refreshToken;
        this.passwordChangeRequired = passwordChangeRequired;
        this.userId = userId;
        this.roleNames = roleNames;
    }
}
