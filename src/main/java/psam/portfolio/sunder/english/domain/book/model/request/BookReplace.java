package psam.portfolio.sunder.english.domain.book.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookReplace {

    @NotNull
    private Boolean openToPublic;

    private String publisher;

    @NotBlank
    private String name;
    private String chapter;
    private String subject;

    public Book toEntity(Academy academy) {
        return Book.builder()
                .openToPublic(openToPublic)
                .publisher(publisher)
                .name(name)
                .chapter(chapter)
                .subject(subject)
                .academy(academy)
                .build();
    }
}