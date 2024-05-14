package psam.portfolio.sunder.english.domain.book.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.book.model.entity.Word;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WordFullResponse {

    private Long id;
    private String korean;
    private String english;

    public static WordFullResponse from(Word word) {
        return WordFullResponse.builder()
                .id(word.getId())
                .korean(word.getKorean())
                .english(word.getEnglish())
                .build();
    }

    public void setKorean(String korean) {
        this.korean = korean;
    }

    public void setEnglish(String english) {
        this.english = english;
    }
}
