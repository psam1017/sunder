package psam.portfolio.sunder.english.domain.user.exception;

import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;

public class UnauthorizedToChangePasswordException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_DATA, User.class, "UNAUTHORIZED_TO_CHANGE_PASSWORD", "비밀번호 변경 권한이 없습니다. 먼저 기존 비밀번호로 인증해주세요.");
    }
}
