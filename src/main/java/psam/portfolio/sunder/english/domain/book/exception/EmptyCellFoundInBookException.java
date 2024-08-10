package psam.portfolio.sunder.english.domain.book.exception;

import psam.portfolio.sunder.english.domain.book.model.entity.Word;
import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class EmptyCellFoundInBookException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return null;
    }

    public EmptyCellFoundInBookException(int row) {
        super.response = ApiResponse.error(
                ApiStatus.ILLEGAL_DATA,
                Word.class,
                "EMPTY_CELL_FOUND_IN_BOOK",
                "파일에 값이 없는 셀이 발견되었습니다. " + row + "행을 확인해보세요."
        );
    }
}
