package psam.portfolio.sunder.english.domain.student.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import psam.portfolio.sunder.english.domain.student.model.embeddable.Parent;
import psam.portfolio.sunder.english.domain.student.model.embeddable.School;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentPATCHInfo {

    @NotBlank
    @Pattern(regexp = "^[가-힣]{2,30}$")
    private String name;

    @Pattern(regexp = "^010[0-9]{8}$")
    private String phone;

    @Email
    private String email;

    private String street;
    private String addressDetail;
    @Pattern(regexp = "^[0-9]{5}$")
    private String postalCode;

    @Length(min = 1, max = 127)
    private String attendanceId;
    private String note;

    private String schoolName;
    private Integer schoolGrade;

    private String parentName;

    @Pattern(regexp = "^010[0-9]{8}$")
    private String parentPhone;

    @JsonIgnore
    public Address getAddress() {
        return Address.builder()
                .street(street)
                .detail(addressDetail)
                .postalCode(postalCode)
                .build();
    }

    @JsonIgnore
    public Parent getParent() {
        return Parent.builder()
                .parentName(parentName)
                .parentPhone(parentPhone)
                .build();
    }

    @JsonIgnore
    public School getSchool() {
        return School.builder()
                .schoolName(schoolName)
                .schoolGrade(schoolGrade)
                .build();
    }
}
