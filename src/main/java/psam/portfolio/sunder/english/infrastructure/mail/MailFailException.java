package psam.portfolio.sunder.english.infrastructure.mail;

import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;

public class MailFailException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.INTERNAL_SERVER_ERROR, MailUtils.class, "failed to send mail");
    }
}
