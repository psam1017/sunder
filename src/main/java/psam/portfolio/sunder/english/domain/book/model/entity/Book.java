package psam.portfolio.sunder.english.domain.book.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.book.model.enumeration.BookStatus;
import psam.portfolio.sunder.english.global.jpa.audit.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "books",
        indexes = @Index(columnList = "academy_id")
)
@Entity
public class Book extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private boolean openToPublic;
    @Enumerated(EnumType.STRING)
    private BookStatus status;

    private String publisher;
    private String bookName;
    private String chapter;
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academy_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Academy academy;

    @SQLRestriction(value = "status != 'DELETED'")
    @OneToMany(mappedBy = "book")
    private List<Word> words = new ArrayList<>();

    @Builder
    public Book(boolean openToPublic, String publisher, String bookName, String chapter, String subject, Academy academy) {
        this.status = BookStatus.CREATED;
        this.openToPublic = openToPublic;
        this.publisher = publisher;
        this.bookName = bookName;
        this.chapter = chapter;
        this.subject = subject;
        this.academy = academy;
    }
}
