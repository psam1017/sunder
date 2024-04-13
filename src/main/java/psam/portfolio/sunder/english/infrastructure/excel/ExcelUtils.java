package psam.portfolio.sunder.english.infrastructure.excel;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
        for (ExcelType excelType : ExcelType.values()) {
            if (Objects.equals(multipartFile.getContentType(), excelType.mimeType()) && Objects.equals(extension, excelType.extension())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 엑셀 파일을 읽어서 데이터를 반환한다. 기본적으로 첫 줄을 제외하고 반환한다.
     * @param multipartFile 엑셀 파일
     * @param headerNames 헤더 이름 목록
     * @return 엑셀 데이터
     * @throws IOException 엑셀 파일 읽기 실패
     */
    public List<List<String>> readExcel(MultipartFile multipartFile, String... headerNames) throws IOException {
        return readExcel(multipartFile, true, false, true, headerNames);
    }

    /**
     * 엑셀 파일을 읽어서 데이터를 반환한다. 첫 줄 포함 여부를 선택할 수 있다.
     * @param multipartFile 엑셀 파일
     * @param includeFirstRow 첫 줄 포함 여부
     * @param returnFirstRow 첫 줄 반환 여부
     * @param inspectFirstRow 첫 줄 검사 여부
     * @param headerNames 헤더 이름 목록
     * @return 엑셀 데이터
     * @throws IOException 엑셀 파일 읽기 실패
     */
    public List<List<String>> readExcel(MultipartFile multipartFile, boolean includeFirstRow, boolean returnFirstRow, boolean inspectFirstRow, String... headerNames) throws IOException {

        // MS 엑셀 파일이 아닌 경우 예외 처리
        if (!isExcelFile(multipartFile)) {
            throw new IllegalExcelTypeException();
        }
        Sheet sheet = createWorkbook(multipartFile).getSheetAt(0);

        // 첫 줄 검사
        if (includeFirstRow && inspectFirstRow) {
            Row firstRow = sheet.getRow(0);
            for (int i = 0; i < headerNames.length; i++) {
                if (!Objects.equals(firstRow.getCell(i).getStringCellValue(), headerNames[i])) {
                    throw new UnmatchedExcelHeaderException();
                }
            }
        }

        // 엑셀 데이터 반환
        List<List<String>> result = new ArrayList<>();
        for (int i = includeFirstRow && returnFirstRow ? 0 : 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            Row row = sheet.getRow(i);
            List<String> elements = new ArrayList<>();
            for (int j = 0; j < headerNames.length; j++) {
                elements.add(row.getCell(j).getStringCellValue());
            }
            result.add(elements);
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
