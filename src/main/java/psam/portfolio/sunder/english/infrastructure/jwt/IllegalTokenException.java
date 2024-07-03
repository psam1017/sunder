package psam.portfolio.sunder.english.infrastructure.jwt;

import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class IllegalTokenException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.FORBIDDEN, JwtStatus.class, "로그인 상태에 문제가 생겼습니다. 다시 로그인해주세요.");
    }
}
