package psam.portfolio.sunder.english.domain.book.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.book.model.entity.Word;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordPUTJson {

    @Valid
    @Size(min = 1, max = 100)
    @NotEmpty
    private List<WordPUT> words;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WordPUT {

        @NotBlank
        private String english;
        @NotBlank
        private String korean;

        public Word toEntity(Book book) {
            return Word.builder()
                    .korean(korean)
                    .english(english)
                    .book(book)
                    .build();
        }
    }
}
