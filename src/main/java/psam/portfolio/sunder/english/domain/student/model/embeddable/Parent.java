package psam.portfolio.sunder.english.domain.student.model.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Parent {

    @Column(name = "parent_name")
    private String name;
    @Column(name = "parent_phone")
    private String phone;

    @Builder
    public Parent(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}
