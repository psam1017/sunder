package psam.portfolio.sunder.english.domain.user.model.response;

import lombok.Getter;

@Getter
public class TokenRefreshResponse {

    private final String type;
    private final String token;

    public TokenRefreshResponse(String token) {
        this.type = "Bearer ";
        this.token = token;
    }
}
