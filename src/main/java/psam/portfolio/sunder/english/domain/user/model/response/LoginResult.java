package psam.portfolio.sunder.english.domain.user.model.response;

import lombok.Getter;

@Getter
public class LoginResult {

    private final String refreshToken;
    private final String accessToken;
    private final boolean passwordChangeRequired;

    public LoginResult(String accessToken, String refreshToken, boolean passwordChangeRequired) {
        this.accessToken = "Bearer " + accessToken;
        this.refreshToken = "Bearer " + refreshToken;
        this.passwordChangeRequired = passwordChangeRequired;
    }
}
