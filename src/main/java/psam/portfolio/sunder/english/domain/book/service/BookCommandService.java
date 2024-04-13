package psam.portfolio.sunder.english.domain.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import psam.portfolio.sunder.english.domain.book.enumeration.BookStatus;
import psam.portfolio.sunder.english.domain.book.enumeration.WordStatus;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.book.model.entity.QBook;
import psam.portfolio.sunder.english.domain.book.model.entity.Word;
import psam.portfolio.sunder.english.domain.book.model.request.BookReplace;
import psam.portfolio.sunder.english.domain.book.model.request.WordPOSTList;
import psam.portfolio.sunder.english.domain.book.repository.BookCommandRepository;
import psam.portfolio.sunder.english.domain.book.repository.BookQueryRepository;
import psam.portfolio.sunder.english.domain.book.repository.WordCommandRepository;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.infrastructure.excel.ExcelUtils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class BookCommandService {

    private final BookCommandRepository bookCommandRepository;
    private final BookQueryRepository bookQueryRepository;
    private final WordCommandRepository wordCommandRepository;

    private final TeacherQueryRepository teacherQueryRepository;

    private final ExcelUtils excelUtils;

    /**
     * 교재 정보 수정 서비스. id 가 없으면 신규 생성, id 가 있다면 수정한다.
     *
     * @param teacherId 사용자 아이디
     * @param bookId    수정할 교재 아이디
     * @param replace   수정할 교재 정보
     * @return 수정에 성공한 교재 아이디
     */
    public UUID replaceBook(UUID teacherId, UUID bookId, BookReplace replace) {
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);

        if (bookId == null) {
            return bookCommandRepository.save(replace.toEntity(getTeacher.getAcademy())).getId();
        } else {
            Book getBook = bookQueryRepository.getOne(
                    QBook.book.id.eq(bookId),
                    QBook.book.academy.eq(getTeacher.getAcademy()),
                    QBook.book.status.ne(BookStatus.DELETED)
            );
            getBook.setOpenToPublic(replace.getOpenToPublic());
            getBook.setPublisher(replace.getPublisher());
            getBook.setBookName(replace.getBookName());
            getBook.setChapter(replace.getChapter());
            getBook.setSubject(replace.getSubject());
            getBook.updateSearchText();
            return getBook.getId();
        }
    }

    /**
     * 교재에 단어 추가 서비스. JSON 형식으로 단어를 추가한다. 기존의 단어들은 논리 삭제된다.
     *
     * @param teacherId 사용자 아이디
     * @param bookId    교재 아이디
     * @param postList  생성/교체할 단어 목록
     * @return 생성/교체된 단어들이 속한 교재 아이디
     */
    public UUID replaceWords(UUID teacherId, UUID bookId, WordPOSTList postList) {
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        Book getBook = bookQueryRepository.getOne(
                QBook.book.id.eq(bookId),
                QBook.book.academy.eq(getTeacher.getAcademy()),
                QBook.book.status.ne(BookStatus.DELETED)
        );
        wordCommandRepository.updateStatusByBookId(WordStatus.DELETED, getBook.getId());

        Book refreshBook = bookQueryRepository.getById(getBook.getId());
        List<Word> saveWords = wordCommandRepository.saveAll(postList.getWords().stream().map(w -> w.toEntity(refreshBook)).toList());
        refreshBook.getWords().addAll(saveWords);

        // 단어 목록의 수정은 실질적으로 교재의 수정이므로 교재의 수정 시간을 강제로 갱신한다.
        refreshBook.updateModifiedDateTimeForcibly();

        return refreshBook.getId();
    }

    /**
     * 교재에 단어 추가 서비스. 엑셀 파일을 업로드하여 단어를 추가한다. 기존의 단어들은 논리 삭제된다.
     *
     * @param teacherId 사용자 아이디
     * @param bookId    교재 아이디
     * @param file      생성/교체할 단어가 입력된 엑셀 파일
     * @return 생성/교체된 단어들이 속한 교재 아이디
     */
    public UUID replaceWords(UUID teacherId, UUID bookId, MultipartFile file) throws IOException {
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        Book getBook = bookQueryRepository.getOne(
                QBook.book.id.eq(bookId),
                QBook.book.academy.eq(getTeacher.getAcademy()),
                QBook.book.status.ne(BookStatus.DELETED)
        );
        wordCommandRepository.updateStatusByBookId(WordStatus.DELETED, getBook.getId());

        Book refreshBook = bookQueryRepository.getById(getBook.getId());
        List<Word> words = excelUtils.readExcel(file, "english", "korean").stream()
                .map(w -> Word.builder()
                        .english(w.get(0))
                        .korean(w.get(1))
                        .book(refreshBook)
                        .build())
                .toList();
        List<Word> saveWords = wordCommandRepository.saveAll(words);
        refreshBook.getWords().addAll(saveWords);

        // 단어 목록의 수정은 실질적으로 교재의 수정이므로 교재의 수정 시간을 강제로 갱신한다.
        refreshBook.updateModifiedDateTimeForcibly();

        return refreshBook.getId();
    }

    /**
     * 교재 삭제 서비스. 교재 삭제 시 단어도 함께 삭제된다.
     *
     * @param teacherId 선생님 아이디
     * @param bookId    삭제할 교재 아이디
     * @return 삭제된 교재 아이디
     */
    public UUID deleteBook(UUID teacherId, UUID bookId) {
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        Book getBook = bookQueryRepository.getOne(
                QBook.book.id.eq(bookId),
                QBook.book.academy.eq(getTeacher.getAcademy()),
                QBook.book.status.ne(BookStatus.DELETED)
        );

        // 반드시 book 을 먼저 삭제할 것. 영속성 컨텍스트가 초기화됨.
        getBook.setStatus(BookStatus.DELETED);
        wordCommandRepository.updateStatusByBookId(WordStatus.DELETED, getBook.getId());
        return bookId;
    }
}
