package psam.portfolio.sunder.english.domain.book.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookReplace {

    @NotNull
    private Boolean openToPublic;

    @Length(max = 30)
    private String publisher;

    @NotBlank
    @Length(max = 30)
    private String name;
    @Length(max = 30)
    private String chapter;
    @Length(max = 30)
    private String subject;

    private Integer schoolGrade;

    public Book toEntity(Academy academy) {
        return Book.builder()
                .openToPublic(openToPublic)
                .publisher(publisher)
                .name(name)
                .chapter(chapter)
                .subject(subject)
                .schoolGrade(schoolGrade)
                .academy(academy)
                .build();
    }
}