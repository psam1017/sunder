package psam.portfolio.sunder.english.domain.teacher.exception;

import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;
import psam.portfolio.sunder.english.domain.teacher.model.entity.Teacher;

public class NoSuchTeacherException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.NO_SUCH_ELEMENT, Teacher.class, "존재하지 않는 선생님입니다.");
    }
}
