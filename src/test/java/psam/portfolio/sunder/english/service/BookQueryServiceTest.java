package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.book.model.request.BookSearchCond;
import psam.portfolio.sunder.english.domain.book.model.response.BookFullResponse;
import psam.portfolio.sunder.english.domain.book.service.BookQueryService;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SuppressWarnings("unchecked")
public class BookQueryServiceTest extends AbstractSunderApplicationTest {

    @Autowired
    BookQueryService sut; // system under test

    @DisplayName("교재 목록을 출판사, 교재명, 챕터, 주제 상관 없이 검색할 수 있다.")
    @Test
    void getBookList() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);

        dataCreator.registerBook(false, "능률(김성곤)", "중3", "1과", "본문", academy);
        dataCreator.registerBook(false, "미래(최연희)", "중3", "1과", "본문", academy);
        dataCreator.registerBook(false, "능률(김성곤)", "중2", "1과", "본문", academy);
        dataCreator.registerBook(false, "능률(김성곤)", "중3", "2과", "본문", academy);
        dataCreator.registerBook(false, "능률(김성곤)", "중3", "1과", "예문", academy);

        BookSearchCond cond = BookSearchCond.builder()
                .keyword("능률 김 중3 1과 본문")
                .privateOnly(false)
                .year(null)
                .build();

        // when
        Map<String, Object> result = refreshAnd(() -> sut.getBookList(teacher.getId(), cond));

        // then
        List<BookFullResponse> books = (List<BookFullResponse>) result.get("books");
        assertThat(books).hasSize(1)
                .extracting("publisher", "bookName", "chapter", "subject", "academyId", "openToPublic")
                .containsExactly(
                        tuple("능률(김성곤)", "중3", "1과", "본문", academy.getId(), false)
                );
    }

    @DisplayName("교재 상세와 함께 교재의 단어 목록을 조회할 수 있다.")
    @Test
    void getBookDetail() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        Book book = dataCreator.registerAnyBook(academy);
        dataCreator.registerWord("apple", "사과", book);
        dataCreator.registerWord("banana", "바나나", book);
        dataCreator.registerWord("cherry", "체리", book);

        // when
        Map<String, Object> result = refreshAnd(() -> sut.getBookDetail(teacher.getId(), academy.getBooks().get(0).getId()));

        // then
        BookFullResponse getBook = (BookFullResponse) result.get("book");
        assertThat(getBook.getPublisher()).isEqualTo(book.getPublisher());
        assertThat(getBook.getBookName()).isEqualTo(book.getBookName());
        assertThat(getBook.getChapter()).isEqualTo(book.getChapter());
        assertThat(getBook.getSubject()).isEqualTo(book.getSubject());
        assertThat(getBook.getAcademyId()).isEqualTo(book.getAcademy().getId());
        assertThat(getBook.isOpenToPublic()).isEqualTo(book.isOpenToPublic());

        List<Map<String, Object>> words = (List<Map<String, Object>>) result.get("words");
        assertThat(words).hasSize(3)
                .extracting("korean", "english")
                .containsExactly(
                        tuple("사과", "apple"),
                        tuple("바나나", "banana"),
                        tuple("체리", "cherry")
                );
    }
}
