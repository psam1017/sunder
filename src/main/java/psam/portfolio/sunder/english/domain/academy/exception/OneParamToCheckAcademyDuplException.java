package psam.portfolio.sunder.english.domain.academy.exception;

import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;

public class OneParamToCheckAcademyDuplException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_DATA, Academy.class, "ONE_PARAM_TO_CHECK_ACADEMY_DUPL", "name, email, phone 중 하나만 입력해야 합니다.");
    }
}
