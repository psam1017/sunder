package psam.portfolio.sunder.english.domain.book.exception;

import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;
import psam.portfolio.sunder.english.domain.book.model.entity.Book;

public class NoSuchBookException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.NO_SUCH_ELEMENT, Book.class, "존재하지 않는 교재입니다.");
    }
}