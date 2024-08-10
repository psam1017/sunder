package psam.portfolio.sunder.english.infrastructure.excel;

import psam.portfolio.sunder.english.global.api.v1.ApiException;
import psam.portfolio.sunder.english.global.api.v1.ApiResponse;
import psam.portfolio.sunder.english.global.api.v1.ApiStatus;

public class IllegalCellTypeException extends ApiException {
    @Override
    public ApiResponse<?> initialize() {
        return null;
    }

    public IllegalCellTypeException(int row, int column) {
        this.response = ApiResponse.error(
                ApiStatus.ILLEGAL_DATA,
                ExcelUtils.class,
                "CELL_TYPE",
                "엑셀 값은 문자와 숫자만 가능합니다. " + row + "행 " + column + "열을 확인해보세요."
        );
    }
}
