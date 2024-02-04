package psam.portfolio.sunder.english.web.teacher.exception;

import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;

public class AcademyAccessDeniedException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ACCESS_DENIAL, Academy.class, "학원 소속 선생님만 접근할 수 있습니다.");
    }
}
