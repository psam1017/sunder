package psam.portfolio.sunder.english.domain.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.domain.book.exception.BookAccessDeniedException;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.book.model.entity.QBook;
import psam.portfolio.sunder.english.domain.book.model.request.BookPageSearchCond;
import psam.portfolio.sunder.english.domain.book.model.request.WordSearchForm;
import psam.portfolio.sunder.english.domain.book.model.response.BookAndWordFullResponse;
import psam.portfolio.sunder.english.domain.book.model.response.BookFullResponse;
import psam.portfolio.sunder.english.domain.book.model.response.RandomWordResponse;
import psam.portfolio.sunder.english.domain.book.model.response.WordFullResponse;
import psam.portfolio.sunder.english.domain.book.repository.BookQueryRepository;
import psam.portfolio.sunder.english.domain.book.repository.WordQueryRepository;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.exception.NotAUserException;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.global.pagination.PageInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookQueryService {

    private static final int PAGE_SET_AMOUNT = 10;
    private static final int TEST_WORD_AMOUNT = 50;

    private final BookQueryRepository bookQueryRepository;
    private final UserQueryRepository userQueryRepository;
    private final WordQueryRepository wordQueryRepository;

    /**
     * 교재 목록 조회 서비스
     *
     * @param userId 사용자 아이디
     * @param cond   교재 목록 조회 조건
     * @return 교재 목록과 페이지 정보
     */
    public Map<String, Object> getBookList(UUID userId, BookPageSearchCond cond) {
        User getUser = userQueryRepository.getById(userId);
        UUID academyId = getAcademyFromUser(getUser).getId();

        List<BookFullResponse> books = bookQueryRepository.findAllDTOByPageSearchCond(academyId, cond);
        long count = bookQueryRepository.countByPageSearchCond(books.size(), academyId, cond);

        return Map.of(
                "books", books,
                "pageInfo", new PageInfo(cond.getPage(), cond.getSize(), count, PAGE_SET_AMOUNT)
        );
    }

    /**
     * 교재 상세 정보와 단어 목록 조회 서비스
     *
     * @param userId 사용자 아이디
     * @param bookId 조회할 교재 아이디
     * @return 교재 상세 정보와 단어 목록
     */
    public BookAndWordFullResponse getBookDetail(UUID userId, UUID bookId) {
        User getUser = userQueryRepository.getById(userId);
        Academy academy = getAcademyFromUser(getUser);

        Book getBook = bookQueryRepository.getById(bookId);
        if (!getBook.canAccess(academy)) {
            throw new BookAccessDeniedException();
        }

        return BookAndWordFullResponse.builder()
                .book(BookFullResponse.from(getBook))
                .words(getBook.getWords().stream().map(WordFullResponse::from).toList())
                .build();
    }

    private static Academy getAcademyFromUser(User getUser) {
        if (getUser instanceof Teacher teacher) {
            return teacher.getAcademy();
        } else if (getUser instanceof Student student) {
            return student.getAcademy();
        }
        throw new NotAUserException();
    }

    /**
     * 교재에 속한 단어 목록을 무작위로 조회 서비스.
     * 주로 시험지 생성을 위해 사용된다.
     *
     * @param teacherId 선생님 아이디
     * @param form      단어 목록 조회 조건
     * @return 조회된 단어 목록과 시험 제목
     */
    public RandomWordResponse findRandomWords(UUID teacherId, WordSearchForm form) {

        User getUser = userQueryRepository.getById(teacherId);
        Academy academy = getAcademyFromUser(getUser);

        QBook qBook = QBook.book;
        List<Book> books = bookQueryRepository.findAll(
                qBook.id.in(form.getBookIds()),
                qBook.academy.id.eq(academy.getId())
                        .or(qBook.academy.id.isNull())
                        .or(qBook.academy.academyShares.any().sharedAcademy.id.eq(academy.getId()))
        );

        books.sort(Comparator.comparing(Book::getName));
        String title = createTitle(books);

        List<UUID> bookIds = books.stream().map(Book::getId).toList();
        List<WordFullResponse> words = wordQueryRepository.findRandomWords(bookIds, TEST_WORD_AMOUNT).stream().map(WordFullResponse::from).toList();

        return RandomWordResponse.builder()
                .title(title)
                .words(words)
                .build();
    }

    private String createTitle(List<Book> books) {
        Book firstBook = books.get(0);
        String title = buildTitleSegment(firstBook);

        if (books.size() > 1) {
            Book lastBook = books.get(books.size() - 1);
            title += " ~ " + buildTitleSegment(lastBook);
        }
        return title;
    }

    private String buildTitleSegment(Book book) {
        StringBuilder titleSegment = new StringBuilder();

        if (StringUtils.hasText(book.getPublisher())) {
            titleSegment.append(book.getPublisher()).append(" ");
        }
        titleSegment.append(book.getName());

        boolean chapterExist = StringUtils.hasText(book.getChapter());
        boolean subjectExist = StringUtils.hasText(book.getSubject());

        if (chapterExist && subjectExist) {
            titleSegment.append("(").append(book.getChapter()).append(" ").append(book.getSubject()).append(")");
        } else if (chapterExist) {
            titleSegment.append("(").append(book.getChapter()).append(")");
        } else if (subjectExist) {
            titleSegment.append("(").append(book.getSubject()).append(")");
        }
        return titleSegment.toString();
    }
}
