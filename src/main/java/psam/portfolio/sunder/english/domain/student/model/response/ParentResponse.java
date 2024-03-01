package psam.portfolio.sunder.english.domain.student.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.student.model.embeddable.Parent;

import static lombok.AccessLevel.PRIVATE;

@Builder
@AllArgsConstructor(access = PRIVATE)
@Getter
public class ParentResponse {

    private String name;
    private String phone;

    public static ParentResponse from(Parent parent) {
        if (parent == null) {
            return new ParentResponse(null, null);
        }
        return ParentResponse.builder()
                .name(parent.getParentName())
                .phone(parent.getParentPhone())
                .build();
    }
}
