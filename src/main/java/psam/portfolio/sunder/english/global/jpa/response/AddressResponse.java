package psam.portfolio.sunder.english.global.jpa.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.global.jpa.embeddable.Address;

import static lombok.AccessLevel.PRIVATE;

@Builder
@AllArgsConstructor(access = PRIVATE)
@Getter
public class AddressResponse {

    private String street;
    private String detail;
    private String postalCode;

    public static AddressResponse from(Address address) {
        if (address == null) {
            return new AddressResponse(null, null, null);
        }
        return AddressResponse.builder()
                .street(address.getStreet())
                .detail(address.getDetail())
                .postalCode(address.getPostalCode())
                .build();
    }
}
