package psam.portfolio.sunder.english.domain.book.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import psam.portfolio.sunder.english.domain.book.model.enumeration.WordStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction(value = "status != 'DELETED'")
@Table(
        name = "words",
        indexes = @Index(columnList = "book_id")
)
@Entity
public class Word {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private WordStatus status;

    private String korean;
    private String english;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "book_id",
            nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Book book;

    @Builder
    public Word(String korean, String english, Book book) {
        this.status = WordStatus.CREATED;
        this.korean = korean;
        this.english = english;
        this.book = book;
    }
}
