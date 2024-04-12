package psam.portfolio.sunder.english.infrastructure.excel;

import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class UnmatchedExcelHeaderException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_DATA, ExcelUtils.class, "UNMATCHED_HEADER", "엑셀 파일의 첫 줄이 지정된 형식과 일치하지 않습니다.");
    }
}
