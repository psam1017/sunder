package psam.portfolio.sunder.english.domain.user.exception;

import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class UserAccessDeniedException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ACCESS_DENIAL, User.class, "사용자 정보에 대한 접근이 거부되었습니다.");
    }
}
