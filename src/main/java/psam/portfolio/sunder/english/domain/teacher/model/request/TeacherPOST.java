package psam.portfolio.sunder.english.domain.teacher.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.model.enumeration.UserStatus;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherPOST {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$")
    private String loginId;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,20}$")
    private String loginPw;

    @NotBlank
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,10}$")
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

    public Teacher toEntity(Academy academy, UserStatus status, String encodeLoginPw) {
        return Teacher.builder()
                .loginId(loginId)
                .loginPw(encodeLoginPw)
                .name(name)
                .email(email)
                .emailVerified(true)
                .phone(phone)
                .address(Address.builder()
                        .street(street)
                        .detail(addressDetail)
                        .postalCode(postalCode)
                        .build())
                .status(status)
                .academy(academy)
                .build();
    }
}
