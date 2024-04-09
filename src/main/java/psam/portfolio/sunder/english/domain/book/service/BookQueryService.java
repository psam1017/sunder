package psam.portfolio.sunder.english.domain.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import psam.portfolio.sunder.english.domain.book.model.request.BookSearchCond;
import psam.portfolio.sunder.english.domain.book.repository.BookQueryRepository;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookQueryService {

    private final BookQueryRepository bookQueryRepository;
    private final UserQueryRepository userQueryRepository;

    public Map<String, Object> getBookList(UUID userId, BookSearchCond cond) {
        return null;
    }

    public Map<String, Object> getBookDetail(UUID userId, UUID bookId) {
        return null;
    }
}
