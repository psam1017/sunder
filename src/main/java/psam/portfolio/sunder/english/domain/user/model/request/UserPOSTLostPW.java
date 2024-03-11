package psam.portfolio.sunder.english.domain.user.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPOSTLostPW {

    @NotBlank
    private String loginId;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String name;
}
