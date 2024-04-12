package psam.portfolio.sunder.english.domain.book.exception;

import psam.portfolio.sunder.english.domain.book.model.entity.Book;
import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class BookAccessDeniedException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ACCESS_DENIAL, Book.class, "교재를 조회할 권한이 없습니다.");
    }
}
