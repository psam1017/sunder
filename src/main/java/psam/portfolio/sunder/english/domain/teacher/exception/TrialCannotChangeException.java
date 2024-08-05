package psam.portfolio.sunder.english.domain.teacher.exception;

import psam.portfolio.sunder.english.domain.user.model.enumeration.UserStatus;
import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class TrialCannotChangeException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_STATUS, UserStatus.class, UserStatus.TRIAL.name(), "체험판 상태일 때는 변경할 수 없습니다.");
    }
}
