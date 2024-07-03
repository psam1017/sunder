package psam.portfolio.sunder.english.domain.study.exception;

import psam.portfolio.sunder.english.domain.study.model.entity.Study;
import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class WordSizeNotEnoughToStudyException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return null;
    }

    public WordSizeNotEnoughToStudyException(int wordSize) {
        super.response = ApiResponse.error(ApiStatus.ILLEGAL_DATA, Study.class, "WORD_SIZE_NOT_ENOUGH_TO_STUDY", "학습할 단어 개수가 너무 적습니다. " + wordSize + "개 이상의 단어를 학습할 수 있도록 조절해주세요.");
    }
}
