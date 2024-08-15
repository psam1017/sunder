package psam.portfolio.sunder.english.infrastructure.excel;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExcelUtils {

    /**
     * 엑셀 파일인지 확인한다.
     * @param multipartFile 엑셀 파일
     * @return 엑셀 파일 여부
     */
    public boolean isExcelFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty() || !StringUtils.hasText(multipartFile.getOriginalFilename())) {
            return false;
        }
        String extension = FileNameUtils.getExtension(multipartFile.getOriginalFilename());
        String contentType = multipartFile.getContentType();
        return ExcelType.isExcelFile(extension, contentType);
    }

    /**
     * 엑셀 파일을 읽어서 데이터를 반환한다. 기본적으로 첫 행을 제외하고 반환한다.
     * @param multipartFile 엑셀 파일
     * @param headerNames 헤더 이름 목록
     * @return 엑셀 데이터
     * @throws IOException 엑셀 파일 읽기 실패
     */
    public List<List<String>> readExcel(MultipartFile multipartFile, String... headerNames) throws IOException {
        return readExcel(multipartFile, 120, true, false, true, headerNames);
    }

    /**
     * 엑셀 파일을 읽어서 데이터를 반환한다. 첫 행 포함 여부를 선택할 수 있다.
     * @param multipartFile 엑셀 파일
     * @param maxRepeatCount 가능한 최대 반복 횟수
     * @param includeFirstRow 첫 행 포함 여부
     * @param returnFirstRow 첫 행 반환 여부
     * @param inspectFirstRow 첫 행 검사 여부
     * @param headerNames 헤더 이름 목록
     * @return 엑셀 데이터
     * @throws IOException 엑셀 파일 읽기 실패
     */
    public List<List<String>> readExcel(MultipartFile multipartFile, int maxRepeatCount, boolean includeFirstRow, boolean returnFirstRow, boolean inspectFirstRow, String... headerNames) throws IOException {

        // MS 엑셀 파일이 아닌 경우 예외 처리
        if (!isExcelFile(multipartFile)) {
            throw new IllegalExcelTypeException();
        }

        Sheet sheet;
        try (Workbook workbook = createWorkbook(multipartFile)) {
            sheet = workbook.getSheetAt(0);
        }

        // 첫 행 검사. 정책상 문자열만을 허용
        if (includeFirstRow && inspectFirstRow) {
            Row firstRow = sheet.getRow(0);
            for (int i = 0; i < headerNames.length; i++) {
                if (!Objects.equals(firstRow.getCell(i).getCellType(), CellType.STRING) || !Objects.equals(firstRow.getCell(i).getStringCellValue(), headerNames[i])) {
                    throw new UnmatchedExcelHeaderException();
                }
            }
        }

        List<List<String>> result = new ArrayList<>();

        // i : 현재 행 번호, maxRepeatCount : 최대 반복 횟수, foundNumberOfRows : 찾은 행 횟수, physicalNumberOfRows : 전체 행 개수
        int i = includeFirstRow && returnFirstRow ? 0 : 1;
        maxRepeatCount = maxRepeatCount <= i ? i + 1 : maxRepeatCount;

        int foundNumberOfRows = i;
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
        if (i >= physicalNumberOfRows) {
            throw new EmptyRowException();
        }

        while (i < maxRepeatCount && foundNumberOfRows < physicalNumberOfRows) {
            Row row = sheet.getRow(i);
            if (row != null) {
                List<String> elements = new ArrayList<>();
                boolean hasAnyCellValue = false;
                for (int j = 0; j < headerNames.length; j++) {
                    Cell cell = row.getCell(j);
                    String cellValue = "";

                    if (cell != null) {
                        // 정책상 문자와 숫자만 허용
                        if (Objects.equals(cell.getCellType(), CellType.NUMERIC))
                            cellValue = String.valueOf(cell.getNumericCellValue());
                        else if (Objects.equals(cell.getCellType(), CellType.STRING)) {
                            cellValue = cell.getStringCellValue();
                        } else {
                            throw new IllegalCellTypeException(i + 1, j + 1);
                        }

                        if (!hasAnyCellValue && StringUtils.hasText(cellValue)) {
                            hasAnyCellValue = true;
                        }
                    }
                    elements.add(cellValue);
                }
                if (hasAnyCellValue) {
                    foundNumberOfRows++;
                    result.add(elements);
                }
            }
            i++;
        }
        return result;
    }

    private static Workbook createWorkbook(MultipartFile multipartFile) throws IOException {
        String extension = FileNameUtils.getExtension(multipartFile.getOriginalFilename());
        try (InputStream inputStream = multipartFile.getInputStream()) {
            if (Objects.equals(extension, "xls")) {
                return new HSSFWorkbook(inputStream);
            } else if (Objects.equals(extension, "xlsx")) {
                return new XSSFWorkbook(inputStream);
            }
        }
        throw new IllegalExcelTypeException();
    }
}
