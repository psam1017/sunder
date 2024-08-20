package psam.portfolio.sunder.english.domain.teacher.exception;

import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;
import psam.portfolio.sunder.english.domain.user.enumeration.RoleName;
import psam.portfolio.sunder.english.domain.user.model.entity.UserRole;

public class RoleDirectorRequiredException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_ROLE, UserRole.class, RoleName.ROLE_DIRECTOR.name(), "학원장만 가능한 서비스입니다.");
    }
}
