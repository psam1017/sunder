package psam.portfolio.sunder.english.domain.teacher.exception;

import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;
import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class TeacherAccessDeniedException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ACCESS_DENIAL, Teacher.class, "교사 정보에 대한 접근이 거부되었습니다.");
    }
}
