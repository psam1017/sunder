package psam.portfolio.sunder.english.domain.academy.exception;

import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;

public class AcademyAccessDeniedException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ACCESS_DENIAL, Academy.class, "같은 학원 소속 사용자만 접근할 수 있습니다.");
    }
}
