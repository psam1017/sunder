package psam.portfolio.sunder.english.domain.book.model.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.global.pagination.PageSearchCond;

@Getter
public class BookPageSearchCond extends PageSearchCond {

    private String keyword;
    private boolean privateOnly;
    private Integer year;

    @Builder
    public BookPageSearchCond(Integer page, Integer size, String prop, String dir, String keyword, Boolean privateOnly, Integer year) {
        super(page, size, prop, dir);
        keyword = substring20AndToLowerCase(keyword);
        this.keyword = removeTwoWhiteSpaces(keyword);
        this.privateOnly = privateOnly == null || privateOnly;
        this.year = year;
    }

    private String substring20AndToLowerCase(String str) {
        if (StringUtils.hasText(str) && str.length() > 20) {
            return str.substring(0, 20).toLowerCase();
        }
        return str;
    }

    private String removeTwoWhiteSpaces(String str) {
        if (StringUtils.hasText(str)) {
            while (str.contains("  ")) {
                str = str.replaceAll("\\s+", " ");
            }
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
