package psam.portfolio.sunder.english.domain.academy.exception;

import psam.portfolio.sunder.english.domain.academy.enumeration.AcademyStatus;
import psam.portfolio.sunder.english.domain.academy.model.entity.Academy;
import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class IllegalStatusAcademyException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return null;
    }

    public IllegalStatusAcademyException(AcademyStatus academyStatus) {
        this.response = ApiResponse.error(ApiStatus.ILLEGAL_STATUS, Academy.class, academyStatus.toString(), "서비스를 이용할 수 없는 상태입니다. [" + academyStatus + "]");
    }
}
