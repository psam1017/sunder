package psam.portfolio.sunder.english.global.command;

import psam.portfolio.sunder.english.global.api.ApiException;
import psam.portfolio.sunder.english.global.api.ApiResponse;
import psam.portfolio.sunder.english.global.api.ApiStatus;

public class CommandException extends ApiException {
    
    // 요청한 CRUDCommand 를 처리할 수 없을 때 생성
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_DATA, CRUDCommand.class, null);
    }
}
