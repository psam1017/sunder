package psam.portfolio.sunder.english.domain.study.model.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class StudyRange {

    @Column(name = "book_publisher")
    private String bookPublisher;
    @Column(name = "book_name")
    private String bookName;
    @Column(name = "book_chapter")
    private String bookChapter;
    @Column(name = "book_subject")
    private String bookSubject;

    @Builder
    public StudyRange(String bookPublisher, String bookName, String bookChapter, String bookSubject) {
        this.bookPublisher = bookPublisher;
        this.bookName = bookName;
        this.bookChapter = bookChapter;
        this.bookSubject = bookSubject;
    }

    public static StudyRange of(Book book) {
        return StudyRange.builder()
                .bookPublisher(book.getPublisher())
                .bookName(book.getName())
                .bookChapter(book.getChapter())
                .bookSubject(book.getSubject())
                .build();
    }
}
