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
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class BookFullResponse {

    private UUID id;
    private String publisher;
    private String name;
    private String chapter;
    private String subject;
    private Integer schoolGrade;
    private UUID academyId;
    private boolean shared;
    @KoreanDateTime
    private LocalDateTime createdDateTime;
    @KoreanDateTime
    private LocalDateTime modifiedDateTime;
    private UUID createdBy;
    private UUID modifiedBy;
    private Integer wordCount;

    public static BookFullResponse from(Book book) {
        return BookFullResponse.builder()
                .id(book.getId())
                .publisher(book.getPublisher())
                .name(book.getName())
                .chapter(book.getChapter())
                .subject(book.getSubject())
                .schoolGrade(book.getSchoolGrade())
                .academyId(book.getAcademy().getId())
                .shared(book.isShared())
                .createdDateTime(book.getCreatedDateTime())
                .modifiedDateTime(book.getModifiedDateTime())
                .createdBy(book.getCreatedBy())
                .modifiedBy(book.getModifiedBy())
                .wordCount(book.getWords().size())
                .build();
    }
}
