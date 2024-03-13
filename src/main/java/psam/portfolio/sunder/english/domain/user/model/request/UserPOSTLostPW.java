package psam.portfolio.sunder.english.domain.user.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPOSTLostPw {

    @NotBlank
    private String loginId;

    @NotBlank
    private String email;

    @NotBlank
    private String name;
}
