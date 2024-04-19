package psam.portfolio.sunder.english.domain.user.model.response;

import lombok.Getter;

@Getter
public class TokenRefreshResponse {

    private final String accessToken;
    private final String refreshToken;

    public TokenRefreshResponse(String accessToken, String refreshToken) {
        this.accessToken = "Bearer " + accessToken;
        this.refreshToken = "Bearer " + refreshToken;
    }
}
