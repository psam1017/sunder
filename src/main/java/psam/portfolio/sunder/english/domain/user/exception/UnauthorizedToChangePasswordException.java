package psam.portfolio.sunder.english.domain.user.exception;

import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class UnauthorizedToChangePasswordException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_DATA, User.class, "UNAUTHORIZED_TO_CHANGE_PASSWORD", "비밀번호를 변경할 수 있는 시간이 지났습니다. 다시 기존 비밀번호로 인증해주세요.");
    }
}
