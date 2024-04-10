package psam.portfolio.sunder.english.domain.book.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.global.pagination.SearchCond;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class BookSearchCond extends SearchCond {

    private String keyword;
    private boolean privateOnly;
    private Integer year;

    @Builder
    public BookSearchCond(Integer page, Integer size, String prop, String dir, String keyword, Boolean privateOnly, Integer year) {
        super(page, size, prop, dir);
        this.keyword = keyword;
        this.privateOnly = privateOnly == null || privateOnly;
        this.year = year;
    }

    public String getKeywordForAgainst() {
        if (StringUtils.hasText(keyword)) {
            return Arrays.stream(keyword.split(" "))
                    .map(s -> "+" + s + "*")
                    .collect(Collectors.joining(" "));
        }
        return null;
    }
}
