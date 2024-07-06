package psam.portfolio.sunder.english.domain.teacher.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherPATCHInfo {

    @NotBlank
    @Pattern(regexp = "^[가-힣]{2,30}$")
    private String name;

    @NotBlank
    @Email
    private String email;

    @Pattern(regexp = "^010[0-9]{8}$")
    private String phone;

    private String street;
    private String addressDetail;
    @Pattern(regexp = "^[0-9]{5}$")
    private String postalCode;

    @JsonIgnore
    public Address getAddress() {
        return Address.builder()
                .street(street)
                .detail(addressDetail)
                .postalCode(postalCode)
                .build();
    }
}
