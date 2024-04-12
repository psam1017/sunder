package psam.portfolio.sunder.english.infrastructure.excel;

import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class IllegalExcelTypeException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return ApiResponse.error(ApiStatus.ILLEGAL_DATA, ExcelType.class, "지원하지 않는 엑셀 파일 형식입니다. 지원하는 형식은 xls, xlsx 입니다.");
    }
}
