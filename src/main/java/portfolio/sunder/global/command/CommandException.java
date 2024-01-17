package portfolio.sunder.global.dto.command;

import portfolio.sunder.global.api.ApiException;
import portfolio.sunder.global.api.ApiResponse;
import portfolio.sunder.global.api.ApiStatus;

public class CommandException extends ApiException {
    
    // 요청한 CRUDCommand 를 처리할 수 없을 때 생성
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_DATA, CRUDCommand.class, null);
    }
}
