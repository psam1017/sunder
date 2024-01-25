package psam.portfolio.sunder.english.web.teacher.exception;

import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;
import psam.portfolio.sunder.english.web.teacher.model.Academy;

public class NoSuchAcademyException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.NO_SUCH_ELEMENT, Academy.class, "존재하지 않는 학원입니다.");
    }
}
