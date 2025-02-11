package psam.portfolio.sunder.english.domain.academy.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AcademyPATCH {

    @NotBlank
    @Pattern(regexp = "^(?=(?:.*[가-힣]){2,})[가-힣 ]{2,30}$")
    private String name;

    @Pattern(regexp = "^010[0-9]{8}$")
    private String phone;

    private String street;
    private String addressDetail;
    @Pattern(regexp = "^[0-9]{5}$")
    private String postalCode;

    @NotNull
    private Boolean openToPublic;

    @JsonIgnore
    public Address getAddress() {
        return Address.builder()
                .street(street)
                .detail(addressDetail)
                .postalCode(postalCode)
                .build();
    }
}
