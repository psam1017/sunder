package psam.portfolio.sunder.english.domain.term.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "terms",
        indexes = {
                @Index(columnList = "term")
        }
)
@Entity
public class Term {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String term;

    // TODO: 2024-07-02 book.searchText 삭제한 후 역치 인덱스 방식으로 검색 엔진 개선
    // TODO: 2024-07-03 study.title 에 역치 인덱스 방식으로 검색 엔진 개선
}
