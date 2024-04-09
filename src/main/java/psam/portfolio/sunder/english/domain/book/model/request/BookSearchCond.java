package psam.portfolio.sunder.english.domain.book.model.request;

import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.global.pagination.SearchCond;

@Getter
public class BookSearchCond extends SearchCond {

    private final String bookName;

    @Builder
    public BookSearchCond(Integer page, Integer size, String prop, String dir, String bookName) {
        super(page, size, prop, dir);
        this.bookName = bookName;
    }
}
