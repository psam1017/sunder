package psam.portfolio.sunder.english.domain.study.exception;

import psam.portfolio.sunder.english.domain.study.model.entity.Study;
import psam.portfolio.sunder.english.domain.study.enumeration.StudyStatus;
import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class IllegalStatusStudyException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return null;
    }

    public IllegalStatusStudyException(StudyStatus studyStatus) {
        this.response = ApiResponse.error(ApiStatus.ILLEGAL_STATUS, Study.class, studyStatus.toString(), "서비스를 이용할 수 없는 상태입니다. [" + studyStatus.value() + "]");
    }
}
