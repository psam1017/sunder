package psam.portfolio.sunder.english.web.teacher.exception;

import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;
import psam.portfolio.sunder.english.web.teacher.model.Teacher;

public class NoSuchTeacherException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.NO_SUCH_ELEMENT, Teacher.class, "존재하지 않는 선생님입니다.");
    }
}
