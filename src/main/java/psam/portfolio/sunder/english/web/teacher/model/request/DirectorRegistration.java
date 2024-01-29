package psam.portfolio.sunder.english.web.teacher.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import psam.portfolio.sunder.english.global.enumpattern.EnumPattern;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;
import psam.portfolio.sunder.english.web.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.web.user.enumeration.RoleName;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.web.user.model.UserRole;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DirectorRegistration {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{4,20}$")
    private String loginId;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,20}$")
    private String loginPw;

    @NotBlank
    @Pattern(regexp = "^[가-힣]{2,10}$")
    private String name;

    @Email
    private String email;

    @Pattern(regexp = "^010[0-9]{8}$")
    private String phone;

    private String street;
    private String detail;
    @Pattern(regexp = "^[0-9]{5}$")
    private String postalCode;

    public Teacher toEntity(Academy academy, String encodeLoginPw) {
        return Teacher.builder()
                .loginId(loginId)
                .loginPw(encodeLoginPw)
                .name(name)
                .email(email)
                .emailVerified(false)
                .phone(phone)
                .address(Address.builder()
                        .street(street)
                        .detail(detail)
                        .postalCode(postalCode)
                        .build())
                .status(UserStatus.PENDING)
                .academy(academy)
                .build();
    }
}
