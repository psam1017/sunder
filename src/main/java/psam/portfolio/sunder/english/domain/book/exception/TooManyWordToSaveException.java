package psam.portfolio.sunder.english.domain.book.exception;

import psam.portfolio.sunder.english.domain.book.model.entity.Word;
import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class TooManyWordToSaveException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return null;
    }

    public TooManyWordToSaveException(int wordSize) {
        super.response = ApiResponse.error(ApiStatus.ILLEGAL_DATA, Word.class, "TOO_MANY_WORD_TO_SAVE", "단어가 너무 많습니다. 저장할 수 있는 단어는 총 " + wordSize + "개입니다.");
    }
}
