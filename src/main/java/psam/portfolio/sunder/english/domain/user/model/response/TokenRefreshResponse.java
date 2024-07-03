package psam.portfolio.sunder.english.domain.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class TokenRefreshResponse {

    private final String accessToken;
}
