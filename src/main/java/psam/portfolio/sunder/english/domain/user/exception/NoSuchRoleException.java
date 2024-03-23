package psam.portfolio.sunder.english.domain.user.exception;

import psam.portfolio.sunder.english.domain.user.model.entity.Role;
import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;

public class NoSuchRoleException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.NO_SUCH_ELEMENT, Role.class, "그러한 권한이 존재하지 않습니다.");
    }
}
