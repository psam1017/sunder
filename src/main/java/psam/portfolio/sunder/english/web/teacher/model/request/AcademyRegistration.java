package psam.portfolio.sunder.english.web.teacher.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.web.teacher.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AcademyRegistration {

    @NotBlank
    @Pattern(regexp = "^[가-힣]{2,30}$")
    private String name;

    @Pattern(regexp = "^010[0-9]{8}$")
    private String phone;

    @Email
    private String email;

    private String street;
    private String detail;
    @Pattern(regexp = "^[0-9]{5}$")
    private String postalCode;

    @NotNull
    private Boolean openToPublic;

    public Academy toEntity() {
        return Academy.builder()
                .name(name)
                .phone(phone)
                .email(email)
                .openToPublic(openToPublic)
                .address(Address.builder()
                        .street(street)
                        .detail(detail)
                        .postalCode(postalCode)
                        .build())
                .status(AcademyStatus.USING)
                .build();
    }
}
