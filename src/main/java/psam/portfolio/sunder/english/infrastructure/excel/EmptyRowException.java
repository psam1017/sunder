package psam.portfolio.sunder.english.infrastructure.excel;

import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class EmptyRowException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_DATA, ExcelUtils.class, "EMPTY_ROW", "(제목을 제외하고) 읽을 수 있는 데이터가 없습니다.");
    }
}
