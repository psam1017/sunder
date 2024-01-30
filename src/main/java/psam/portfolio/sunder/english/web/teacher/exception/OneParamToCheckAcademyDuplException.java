package psam.portfolio.sunder.english.web.teacher.exception;

import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;
import psam.portfolio.sunder.english.web.teacher.model.entity.Academy;

public class OneParamToCheckAcademyDuplException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_DATA, Academy.class, "ONE_PARAM_TO_CHECK_ACADEMY_DUPL", "name, email, phone 중 하나만 입력해야 합니다.");
    }
}
