package psam.portfolio.sunder.english.web.user.exception;

import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;
import psam.portfolio.sunder.english.web.user.model.User;

public class OneParamToCheckDuplException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_DATA, User.class, "ONE_PARAM_TO_CHECK_DUPL", "uid, email, phone 중 하나만 입력해야 합니다.");
    }
}
