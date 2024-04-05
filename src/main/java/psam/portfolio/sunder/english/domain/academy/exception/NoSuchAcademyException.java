package psam.portfolio.sunder.english.domain.academy.exception;

import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;

public class NoSuchAcademyException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.NO_SUCH_ELEMENT, Academy.class, "존재하지 않는 학원입니다.");
    }
}
