package psam.portfolio.sunder.english.domain.user.exception;

import psam.portfolio.sunder.english.domain.user.enumeration.UserStatus;
import psam.portfolio.sunder.english.domain.user.model.entity.User;
import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;

public class IllegalStatusUserException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return null;
    }

    public IllegalStatusUserException(UserStatus userStatus) {
        this.response = ApiResponse.error(ApiStatus.ILLEGAL_STATUS, User.class, userStatus.toString(), "서비스를 이용할 수 없는 상태입니다. [" + userStatus + "]");
    }
}
