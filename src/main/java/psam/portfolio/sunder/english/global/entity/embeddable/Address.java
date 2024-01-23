package psam.portfolio.sunder.english.global.entity.embeddable;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Embeddable
public class Address {

    private String street;
    private String detail;
    private String postcode;

    @Builder
    public Address(String street, String detail, String postcode) {
        this.street = street;
        this.detail = detail;
        this.postcode = postcode;
    }
}
