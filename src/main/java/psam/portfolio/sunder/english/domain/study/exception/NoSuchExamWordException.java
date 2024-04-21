package psam.portfolio.sunder.english.domain.study.exception;

import psam.portfolio.sunder.english.domain.study.model.entity.ExamWord;
import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class NoSuchExamWordException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.NO_SUCH_ELEMENT, ExamWord.class, "존재하지 않는 시험 단어입니다.");
    }
}
