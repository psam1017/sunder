package psam.portfolio.sunder.english.domain.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.book.exception.EmptyCellFoundInBookException;
import psam.portfolio.sunder.english.domain.book.exception.TooManyWordToSaveException;
import psam.portfolio.sunder.english.domain.book.enumeration.BookStatus;
import psam.portfolio.sunder.english.domain.book.enumeration.WordStatus;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.book.model.entity.QBook;
import psam.portfolio.sunder.english.domain.book.model.entity.Word;
import psam.portfolio.sunder.english.domain.book.model.request.BookReplace;
import psam.portfolio.sunder.english.domain.book.model.request.WordPUT;
import psam.portfolio.sunder.english.domain.book.repository.BookCommandRepository;
import psam.portfolio.sunder.english.domain.book.repository.BookQueryRepository;
import psam.portfolio.sunder.english.domain.book.repository.WordCommandRepository;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.teacher.repository.TeacherQueryRepository;
import psam.portfolio.sunder.english.infrastructure.excel.ExcelUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class BookCommandService {

    // 변경 시 WordPOSTList.words 의 size 도 변경해야 함.
    private static final int MAX_WORD_SIZE = 100;

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
        Academy getAcademy = getTeacher.getAcademy();

        if (bookId == null) {
            Book saveBook = bookCommandRepository.save(replace.toEntity(getAcademy));
            getAcademy.getBooks().add(saveBook);
            return saveBook.getId();
        } else {
            Book getBook = bookQueryRepository.getOne(
                    QBook.book.id.eq(bookId),
                    QBook.book.academy.eq(getAcademy)
            );
            getBook.setShared(replace.getShared());
            getBook.setPublisher(replace.getPublisher());
            getBook.setName(replace.getName());
            getBook.setChapter(replace.getChapter());
            getBook.setSubject(replace.getSubject());
            getBook.setSchoolGrade(replace.getSchoolGrade());
            getBook.updateSearchText();
            return getBook.getId();
        }
    }

    /**
     * 교재에 단어 등록 서비스. JSON 형식으로 단어를 추가한다. 기존의 단어들은 논리 삭제된다.
     *
     * @param teacherId 사용자 아이디
     * @param bookId    교재 아이디
     * @param put       생성/교체할 단어 목록
     * @return 생성/교체된 단어들이 속한 교재 아이디
     */
    public UUID replaceWords(UUID teacherId, UUID bookId, WordPUT put) {
        Teacher getTeacher = teacherQueryRepository.getById(teacherId);
        Book getBook = bookQueryRepository.getOne(
                QBook.book.id.eq(bookId),
                QBook.book.academy.eq(getTeacher.getAcademy())
        );
        wordCommandRepository.updateStatusByBookId(WordStatus.DELETED, getBook.getId());

        // persistence context is cleared automatically after update bulk-query
        Book refreshBook = bookQueryRepository.getById(getBook.getId());
        List<Word> saveWords = wordCommandRepository.saveAll(put.getWords().stream().map(w -> w.toEntity(refreshBook)).toList());
        refreshBook.getWords().addAll(saveWords);

        // 단어 목록의 수정은 실질적으로 교재의 수정이므로 교재의 수정 시간을 수동으로 갱신한다.
        refreshBook.updateModifiedDateTimeManually();

        return refreshBook.getId();
    }

    /**
     * 교재에 단어 등록 서비스. 엑셀 파일을 업로드하여 단어를 추가한다. 기존의 단어들은 논리 삭제된다.
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
                QBook.book.academy.eq(getTeacher.getAcademy())
        );
        wordCommandRepository.updateStatusByBookId(WordStatus.DELETED, getBook.getId());

        Book refreshBook = bookQueryRepository.getById(getBook.getId());
        List<Word> buildWords = new ArrayList<>();
        List<List<String>> readExcel = excelUtils.readExcel(file, "english", "korean");

        for (int i = 0; i < readExcel.size(); i++) {
            List<String> w = readExcel.get(i);
            if (StringUtils.hasText(w.get(0)) && StringUtils.hasText(w.get(1))) {
                Word buildWord = Word.builder()
                        .english(w.get(0))
                        .korean(w.get(1))
                        .book(refreshBook)
                        .build();
                buildWords.add(buildWord);
            } else {
                throw new EmptyCellFoundInBookException(i + 2);
            }
        }

        if (readExcel.size() > MAX_WORD_SIZE) {
            throw new TooManyWordToSaveException(MAX_WORD_SIZE);
        }

        List<Word> saveWords = wordCommandRepository.saveAll(buildWords);
        refreshBook.getWords().addAll(saveWords);

        // 단어 목록의 수정은 실질적으로 교재의 수정이므로 교재의 수정 시간을 강제로 갱신한다.
        refreshBook.updateModifiedDateTimeManually();

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
                QBook.book.academy.eq(getTeacher.getAcademy())
        );

        // 반드시 book 을 먼저 삭제할 것. 영속성 컨텍스트가 초기화됨.
        getBook.setStatus(BookStatus.DELETED);
        wordCommandRepository.updateStatusByBookId(WordStatus.DELETED, getBook.getId());
        return bookId;
    }
}
