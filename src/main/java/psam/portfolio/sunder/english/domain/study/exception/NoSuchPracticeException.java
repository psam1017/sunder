package psam.portfolio.sunder.english.domain.study.exception;

import psam.portfolio.sunder.english.domain.study.model.entity.Practice;
import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class NoSuchPracticeException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.NO_SUCH_ELEMENT, Practice.class, "존재하지 않는 연습입니다.");
    }
}
