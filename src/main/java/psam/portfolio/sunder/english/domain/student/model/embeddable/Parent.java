package psam.portfolio.sunder.english.domain.student.model.embeddable;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Parent {

    private String parentName;
    private String parentPhone;

    @Builder
    public Parent(String parentName, String parentPhone) {
        this.parentName = parentName;
        this.parentPhone = parentPhone;
    }
}
