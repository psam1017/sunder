package portfolio.sunder.infrastructure.jwt;

import portfolio.sunder.global.api.ApiException;
import portfolio.sunder.global.api.ApiResponse;
import portfolio.sunder.global.api.ApiStatus;

public class IllegalTokenException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_STATUS, JwtStatus.class, null);
    }
}
