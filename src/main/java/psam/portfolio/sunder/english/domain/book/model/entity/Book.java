package psam.portfolio.sunder.english.domain.book.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.book.enumeration.BookStatus;
import psam.portfolio.sunder.english.global.jpa.audit.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private boolean shared;
    @Enumerated(EnumType.STRING)
    private BookStatus status;

    private String publisher;
    private String name;
    private String chapter;
    private String subject;
    private String searchText;
    private Integer schoolGrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academy_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Academy academy;

    @OrderBy("id asc")
    @OneToMany(mappedBy = "book")
    private List<Word> words = new ArrayList<>();

    @Builder
    public Book(boolean shared, String publisher, String name, String chapter, String subject, Integer schoolGrade, Academy academy) {
        this.status = BookStatus.CREATED;
        this.shared = shared;
        this.publisher = publisher;
        this.name = name;
        this.chapter = chapter;
        this.subject = subject;
        this.schoolGrade = schoolGrade;
        this.academy = academy;
        updateSearchText();
    }

    public void setShared(boolean openToPublic) {
        this.shared = openToPublic;
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

    public void setSchoolGrade(Integer schoolGrade) {
        this.schoolGrade = schoolGrade;
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
            if (s != null) {
                searchText.append(s.replaceAll(" ", ""));
            }
        }
        this.searchText = searchText.toString().toLowerCase();
    }

    public boolean canAccess(Academy academy) {
        return this.academy == null || this.academy.isSameOrShared(academy);
    }
}
