package psam.portfolio.sunder.english.domain.student.exception;

import psam.portfolio.sunder.english.domain.student.model.entity.Student;
import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class DuplicateAttendanceIdException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.DUPLICATE_KEY, Student.class, "ATTENDANCE_ID", "이미 등록된 출석 아이디입니다.");
    }
}
