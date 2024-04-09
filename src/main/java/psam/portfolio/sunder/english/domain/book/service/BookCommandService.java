package psam.portfolio.sunder.english.domain.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import psam.portfolio.sunder.english.domain.book.model.request.BookReplace;
import psam.portfolio.sunder.english.domain.book.model.request.WordPOSTList;
import psam.portfolio.sunder.english.domain.book.repository.BookCommandRepository;
import psam.portfolio.sunder.english.domain.book.repository.BookQueryRepository;
import psam.portfolio.sunder.english.domain.book.repository.WordCommandRepository;
import psam.portfolio.sunder.english.domain.book.repository.WordQueryRepository;
import psam.portfolio.sunder.english.domain.user.repository.UserQueryRepository;

import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class BookCommandService {

    private final BookCommandRepository bookCommandRepository;
    private final BookQueryRepository bookQueryRepository;
    private final WordCommandRepository wordCommandRepository;
    private final WordQueryRepository wordQueryRepository;

    private final UserQueryRepository userQueryRepository;

    public UUID replaceBook(UUID userId, UUID bookId, BookReplace replace) {
        return null;
    }

    public UUID replaceWords(UUID userId, UUID bookId, WordPOSTList postList) {
        return null;
    }

    public UUID replaceWords(UUID userId, UUID bookId, MultipartFile file) {
        return null;
    }

    public UUID deleteBook(UUID bookId, UUID id) {
        return null;
    }
}
