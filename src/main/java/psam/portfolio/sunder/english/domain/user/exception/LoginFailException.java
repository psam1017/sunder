package psam.portfolio.sunder.english.domain.user.exception;

import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;

public class LoginFailException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_DATA, User.class, "LOGIN_FAIL", "로그인에 실패하였습니다. 아이디와 비밀번호를 확인해주세요.");
    }
}
