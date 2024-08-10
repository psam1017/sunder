package psam.portfolio.sunder.english.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import psam.portfolio.sunder.english.AbstractSunderApplicationTest;
import psam.portfolio.sunder.english.domain.academy.model.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.book.model.enumeration.BookStatus;
import psam.portfolio.sunder.english.domain.book.exception.NoSuchBookException;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.book.model.request.BookReplace;
import psam.portfolio.sunder.english.domain.book.model.request.WordPUTJson;
import psam.portfolio.sunder.english.domain.book.repository.BookQueryRepository;
import psam.portfolio.sunder.english.domain.book.service.BookCommandService;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.model.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.model.enumeration.UserStatus;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static psam.portfolio.sunder.english.domain.book.model.request.WordPUTJson.*;

public class BookCommandServiceTest extends AbstractSunderApplicationTest {

    @Autowired
    BookCommandService sut; // system under test

    @Autowired
    BookQueryRepository bookQueryRepository;

    @DisplayName("bookId 가 없으면 새로운 교재를 생성할 수 있다.")
    @Test
    void replaceBookWithoutBookId() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);

        String publisher = "publisher";
        String name = "name";
        String chapter = "chapter";
        String subject = "subject";

        BookReplace replace = BookReplace.builder()
                .openToPublic(false)
                .publisher(publisher)
                .name(name)
                .chapter(chapter)
                .subject(subject)
                .build();

        // when
        UUID bookId = refreshAnd(() -> sut.replaceBook(teacher.getId(), null, replace));

        // then
        Book getBook = bookQueryRepository.getById(bookId);
        assertThat(getBook.getPublisher()).isEqualTo(publisher);
        assertThat(getBook.getName()).isEqualTo(name);
        assertThat(getBook.getChapter()).isEqualTo(chapter);
        assertThat(getBook.getSubject()).isEqualTo(subject);

        assertThat(getBook.getAcademy().getId()).isEqualTo(academy.getId());
    }

    @DisplayName("bookId 가 있으면 해당 교재를 수정할 수 있다.")
    @Test
    void replaceBookWithBookId() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        Book book = dataCreator.registerBook(true, "publisher", "name", "chapter", "subject", academy);

        boolean openToPublic = false;
        String publisher = "updatedPublisher";
        String name = "updatedName";
        String chapter = "updatedChapter";
        String subject = "updatedSubject";

        BookReplace replace = BookReplace.builder()
                .openToPublic(openToPublic)
                .publisher(publisher)
                .name(name)
                .chapter(chapter)
                .subject(subject)
                .build();

        // when
        UUID bookId = refreshAnd(() -> sut.replaceBook(teacher.getId(), book.getId(), replace));

        // then
        Book getBook = bookQueryRepository.getById(bookId);
        assertThat(getBook.isOpenToPublic()).isEqualTo(openToPublic);
        assertThat(getBook.getPublisher()).isEqualTo(publisher);
        assertThat(getBook.getName()).isEqualTo(name);
        assertThat(getBook.getChapter()).isEqualTo(chapter);
        assertThat(getBook.getSubject()).isEqualTo(subject);

        assertThat(getBook.getAcademy().getId()).isEqualTo(academy.getId());
    }

    @DisplayName("삭제된 교재는 수정할 수 없다.")
    @Test
    void replaceDeletedBook() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        Book book = dataCreator.registerAnyBook(academy);
        book.setStatus(BookStatus.DELETED);

        BookReplace replace = BookReplace.builder()
                .openToPublic(false)
                .publisher("updatedPublisher")
                .name("updatedName")
                .chapter("updatedChapter")
                .subject("updatedSubject")
                .build();

        // when
        // then
        assertThatThrownBy(() -> sut.replaceBook(teacher.getId(), book.getId(), replace))
                .isInstanceOf(NoSuchBookException.class);
    }

    @DisplayName("DTO 로 단어 목록을 갱신할 수 있다.")
    @Test
    void replaceWords() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        Book book = dataCreator.registerAnyBook(academy);

        WordPUTJson put = WordPUTJson.builder()
                .words(List.of(
                        new WordPUT("apple", "사과"),
                        new WordPUT("banana", "바나나"),
                        new WordPUT("cherry", "체리")
                ))
                .build();

        // when
        UUID bookId = refreshAnd(() -> sut.replaceWords(teacher.getId(), book.getId(), put));

        // then
        Book getBook = bookQueryRepository.getById(bookId);
        assertThat(getBook.getWords()).hasSize(3)
                .extracting("english", "korean")
                .containsExactlyInAnyOrder(
                        tuple("apple", "사과"),
                        tuple("banana", "바나나"),
                        tuple("cherry", "체리")
                );
    }

    // 모듈과 실제 파일이 필요한 기능이므로 단위 테스트를 최소화하고, 통합 테스트를 진행
    // 여유가 된다면 추후에 단위 테스트를 추가 작성
    @DisplayName("엑셀 파일로 단어 목록을 갱신할 수 있다.")
    @Test
    void replaceWordsWithExcelFile() throws IOException {
        // mocking
        given(excelUtils.readExcel(any(), any(), any()))
                .willReturn(List.of(
                        List.of("english1", "korean1"),
                        List.of("english2", "korean2"),
                        List.of("english3", "korean3")
                ));

        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        Book book = dataCreator.registerAnyBook(academy);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "test".getBytes()
        );

        // when
        UUID bookId = refreshAnd(() -> {
            try {
                return sut.replaceWords(teacher.getId(), book.getId(), file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // then
        Book getBook = bookQueryRepository.getById(bookId);
        assertThat(getBook.getWords()).hasSize(3)
                .extracting("korean", "english")
                .containsExactlyInAnyOrder(
                        tuple("korean1", "english1"),
                        tuple("korean2", "english2"),
                        tuple("korean3", "english3")
                );
    }

    @DisplayName("교재를 삭제할 수 있다.")
    @Test
    void deleteBook() {
        // given
        Academy academy = dataCreator.registerAcademy(AcademyStatus.VERIFIED);
        Teacher teacher = dataCreator.registerTeacher(UserStatus.ACTIVE, academy);
        dataCreator.createUserRoles(teacher, RoleName.ROLE_TEACHER);
        Book book = dataCreator.registerAnyBook(academy);
        dataCreator.registerWord("english", "korean", book);

        // when
        UUID bookId = refreshAnd(() -> sut.deleteBook(teacher.getId(), book.getId()));

        // then
        Optional<Book> optBook = bookQueryRepository.findById(bookId);
        assertThat(optBook.isEmpty()).isTrue();
    }
}
