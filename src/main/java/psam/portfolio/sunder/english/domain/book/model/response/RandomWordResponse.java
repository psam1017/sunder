package psam.portfolio.sunder.english.domain.book.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomWordResponse {

    private String title;
    private List<WordFullResponse> words;
}
