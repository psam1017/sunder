package psam.portfolio.sunder.english.domain.teacher.exception;

import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class SelfRoleModificationException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_DATA, Teacher.class, "SELF_ROLE_MODIFICATION", "자기 자신의 권한을 변경할 수 없습니다.");
    }
}
