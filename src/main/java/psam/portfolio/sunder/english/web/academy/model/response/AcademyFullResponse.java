package psam.portfolio.sunder.english.web.academy.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.global.jpa.response.AddressResponse;
import psam.portfolio.sunder.english.global.jsonformat.KoreanDateTime;
import psam.portfolio.sunder.english.web.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.web.academy.model.entity.Academy;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Builder
@Getter
@AllArgsConstructor(access = PRIVATE)
public class AcademyFullResponse {

    private UUID id;
    private String name;
    private AddressResponse address;
    private String phone;
    private String email;
    private boolean openToPublic;
    private AcademyStatus status;
    @KoreanDateTime
    private LocalDateTime createdDateTime;
    @KoreanDateTime
    private LocalDateTime modifiedDateTime;

    public static AcademyFullResponse from(Academy academy) {
        return AcademyFullResponse.builder()
                .id(academy.getUuid())
                .name(academy.getName())
                .address(AddressResponse.from(academy.getAddress()))
                .phone(academy.getPhone())
                .email(academy.getEmail())
                .openToPublic(academy.isOpenToPublic())
                .status(academy.getStatus())
                .createdDateTime(academy.getCreatedDateTime())
                .modifiedDateTime(academy.getModifiedDateTime())
                .build();
    }
}
