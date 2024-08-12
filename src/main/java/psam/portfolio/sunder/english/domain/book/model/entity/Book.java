package psam.portfolio.sunder.english.domain.book.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.book.model.enumeration.BookStatus;
import psam.portfolio.sunder.english.global.jpa.audit.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction(value = "status != 'DELETED'")
@Table(
        name = "books",
        indexes = {
                @Index(columnList = "academy_id"),
                @Index(columnList = "created_date_time")
        }
)
@Entity
public class Book extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private boolean openToPublic;
    @Enumerated(EnumType.STRING)
    private BookStatus status;

    private String publisher;
    private String name;
    private String chapter;
    private String subject;
    private String searchText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academy_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Academy academy;

    @OrderBy("id asc")
    @OneToMany(mappedBy = "book")
    private List<Word> words = new ArrayList<>();

    @Builder
    public Book(boolean openToPublic, String publisher, String name, String chapter, String subject, Academy academy) {
        this.status = BookStatus.CREATED;
        this.openToPublic = openToPublic;
        this.publisher = publisher;
        this.name = name;
        this.chapter = chapter;
        this.subject = subject;
        this.academy = academy;
        updateSearchText();
    }

    public void setOpenToPublic(boolean openToPublic) {
        this.openToPublic = openToPublic;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void updateModifiedDateTimeManually() {
        super.setModifiedDateTime(LocalDateTime.now());
    }

    public void updateSearchText() {
        updateSearchText(publisher, name, chapter, subject);
    }

    private void updateSearchText(String... strings) {
        StringBuilder searchText = new StringBuilder();
        for (String s : strings) {
            searchText.append(s.replaceAll(" ", ""));
        }
        this.searchText = searchText.toString().toLowerCase();
    }

    public boolean isSameAcademyOrPublic(Academy academy) {
        return openToPublic || this.academy == null || Objects.equals(this.academy.getId(), academy.getId());
    }
}
