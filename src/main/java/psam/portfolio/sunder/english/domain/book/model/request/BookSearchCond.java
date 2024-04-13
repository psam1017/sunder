package psam.portfolio.sunder.english.domain.book.model.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.global.pagination.SearchCond;

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
        keyword = substring20(keyword);
        this.keyword = removeTwoWhiteSpaces(keyword);
        this.privateOnly = privateOnly == null || privateOnly;
        this.year = year;
    }

    private String substring20(String str) {
        if (StringUtils.hasText(str) && str.length() > 20) {
            return str.substring(0, 20);
        }
        return str;
    }

    private String removeTwoWhiteSpaces(String str) {
        if (StringUtils.hasText(str)) {
            return str.trim().replaceAll("\\s{2,}", " ");
        }
        return str;
    }

    public String[] getSplitKeyword() {
        if (StringUtils.hasText(keyword)) {
            return keyword.toLowerCase().split(" ");
        }
        return null;
    }
}
