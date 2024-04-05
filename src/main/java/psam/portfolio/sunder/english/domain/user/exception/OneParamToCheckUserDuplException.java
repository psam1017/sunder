package psam.portfolio.sunder.english.domain.user.exception;

import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;
import psam.portfolio.sunder.english.domain.user.model.entity.User;

public class OneParamToCheckUserDuplException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_DATA, User.class, "ONE_PARAM_TO_CHECK_USER_DUPL", "loginId, email, phone 중 하나만 입력해야 합니다.");
    }
}
