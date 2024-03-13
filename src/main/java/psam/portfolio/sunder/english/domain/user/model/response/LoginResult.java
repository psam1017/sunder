package psam.portfolio.sunder.english.domain.user.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResult {

    private String token;
    private boolean passwordChangeRequired;
}
