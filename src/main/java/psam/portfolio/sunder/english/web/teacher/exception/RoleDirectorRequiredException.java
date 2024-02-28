package psam.portfolio.sunder.english.web.teacher.exception;

import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;
import psam.portfolio.sunder.english.web.user.enumeration.RoleName;
import psam.portfolio.sunder.english.web.user.model.entity.UserRole;

public class RoleDirectorRequiredException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_ROLE, UserRole.class, RoleName.ROLE_DIRECTOR.name(), "학원장만 가능한 서비스입니다.");
    }
}
