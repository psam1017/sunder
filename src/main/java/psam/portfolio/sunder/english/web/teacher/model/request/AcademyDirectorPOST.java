package psam.portfolio.sunder.english.web.teacher.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;
import psam.portfolio.sunder.english.web.teacher.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;
import psam.portfolio.sunder.english.web.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.web.user.enumeration.UserStatus;

@Getter
@NoArgsConstructor
public class AcademyDirectorPOST {

    @Valid
    private AcademyPOST academyPOST;

    @Valid
    private DirectorPOST directorPOST;

    @Getter
    @NoArgsConstructor
    public static class AcademyPOST {

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
                    .status(AcademyStatus.PENDING)
                    .build();
        }

        @Builder
        public AcademyPOST(String name, String phone, String email, String street, String detail, String postalCode, Boolean openToPublic) {
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.street = street;
            this.detail = detail;
            this.postalCode = postalCode;
            this.openToPublic = openToPublic;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class DirectorPOST {

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

        @Builder
        public DirectorPOST(String loginId, String loginPw, String name, String email, String phone, String street, String detail, String postalCode) {
            this.loginId = loginId;
            this.loginPw = loginPw;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.street = street;
            this.detail = detail;
            this.postalCode = postalCode;
        }
    }
}
