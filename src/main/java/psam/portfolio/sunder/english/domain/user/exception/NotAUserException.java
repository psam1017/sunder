package psam.portfolio.sunder.english.domain.user.exception;

import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;
import psam.portfolio.sunder.english.domain.user.model.entity.User;

public class NotAUserException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_DATA, User.class, "NOT_A_USER", "올바른 형태의 사용자가 아닙니다.");
    }
}
