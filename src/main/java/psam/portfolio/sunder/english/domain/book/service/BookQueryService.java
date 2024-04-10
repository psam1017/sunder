package psam.portfolio.sunder.english.domain.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.domain.book.model.request.BookSearchCond;
import psam.portfolio.sunder.english.domain.book.model.response.BookFullResponse;
import psam.portfolio.sunder.english.domain.book.repository.BookQueryRepository;
import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.domain.user.exception.NotAUserException;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;
import psam.portfolio.sunder.english.global.pagination.PageInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookQueryService {

    private final static int PAGE_SET_AMOUNT = 10;

    private final BookQueryRepository bookQueryRepository;
    private final UserQueryRepository userQueryRepository;

    /**
     * 교재 목록 조회 서비스
     *
     * @param userId 사용자 아이디
     * @param cond   교재 목록 조회 조건
     * @return 교재 목록과 페이지 정보
     */
    public Map<String, Object> getBookList(UUID userId, BookSearchCond cond) {
        User getUser = userQueryRepository.getById(userId);
        UUID academyId = getAcademyId(cond.isPrivateOnly(), getUser);

        List<Book> books = bookQueryRepository.findAllBySearchCond(academyId, cond);
        long count = bookQueryRepository.countBySearchCond(books.size(), academyId, cond);

        return Map.of(
                "books", books.stream().map(BookFullResponse::from).toList(),
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
    public Map<String, Object> getBookDetail(UUID userId, UUID bookId) {
        return null;
    }

    private static UUID getAcademyId(boolean privateOnly, User getUser) {
        if (privateOnly) {
            if (getUser instanceof Teacher teacher) {
                return teacher.getAcademy().getId();
            } else if (getUser instanceof Student student) {
                return student.getAcademy().getId();
            }
        }
        throw new NotAUserException();
    }
}
