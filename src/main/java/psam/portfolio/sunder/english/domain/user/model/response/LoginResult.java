package psam.portfolio.sunder.english.domain.user.model.response;

import lombok.Getter;

@Getter
public class LoginResult {

    private String type;
    private String token;
    private boolean passwordChangeRequired;

    public LoginResult(String token, boolean passwordChangeRequired) {
        this.type = "Bearer ";
        this.token = token;
        this.passwordChangeRequired = passwordChangeRequired;
    }
}
