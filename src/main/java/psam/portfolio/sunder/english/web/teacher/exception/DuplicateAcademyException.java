package psam.portfolio.sunder.english.web.teacher.exception;

import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;

public class DuplicateAcademyException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.DUPLICATE_KEY, Academy.class, "중복된 학원 정보가 있습니다.");
    }
}
