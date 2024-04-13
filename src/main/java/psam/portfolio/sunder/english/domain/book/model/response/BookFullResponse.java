package psam.portfolio.sunder.english.domain.book.model.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.global.jsonformat.KoreanDateTime;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookFullResponse {

    private UUID id;
    private String publisher;
    private String bookName;
    private String chapter;
    private String subject;
    private UUID academyId;
    private boolean openToPublic;
    @KoreanDateTime
    private LocalDateTime createdDateTime;
    @KoreanDateTime
    private LocalDateTime modifiedDateTime;
    private UUID createdBy;
    private UUID modifiedBy;

    public static BookFullResponse from(Book book) {
        return BookFullResponse.builder()
                .id(book.getId())
                .publisher(book.getPublisher())
                .bookName(book.getBookName())
                .chapter(book.getChapter())
                .subject(book.getSubject())
                .academyId(book.getAcademy().getId())
                .openToPublic(book.isOpenToPublic())
                .createdDateTime(book.getCreatedDateTime())
                .modifiedDateTime(book.getModifiedDateTime())
                .createdBy(book.getCreatedBy())
                .modifiedBy(book.getModifiedBy())
                .build();
    }
}
