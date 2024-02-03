package psam.portfolio.sunder.english.infrastructure.jwt;

import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;

public class IllegalTokenException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.FORBIDDEN, JwtStatus.class, null);
    }
}
