package psam.portfolio.sunder.english.others.infrastructure.excel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import psam.portfolio.sunder.english.infrastructure.excel.ExcelUtils;
import psam.portfolio.sunder.english.infrastructure.excel.UnmatchedExcelHeaderException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("SpellCheckingInspection")
public class ExcelUtilsTest {

    /*
    classpath:static/words.xlsx

    english, korean
    apple, 사과
    banana, 바나나
    car, 자동차
    dog, 개
    eat, 먹다
     */

    private final ExcelUtils excelUtils = new ExcelUtils();

    @DisplayName("엑셀 파일의 확장자를 검사할 수 있다.")
    @Test
    void inspectExcelExtension() throws IOException {
        // given
        File file = new ClassPathResource("static/words.xlsx").getFile();

        MockMultipartFile mockMultipartFile;
        try (FileInputStream fis = new FileInputStream(file)) {
            mockMultipartFile = new MockMultipartFile(
                    "file",
                    "words.xxx", // 확장자를 잘못 지정
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    fis
            );
        }

        // when
        boolean result = excelUtils.isExcelFile(mockMultipartFile);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("엑셀 파일의 mime type 을 검사할 수 있다.")
    @Test
    void inspectExcelContentType() throws IOException {
        // given
        File file = new ClassPathResource("static/words.xlsx").getFile();

        MockMultipartFile mockMultipartFile;
        try (FileInputStream fis = new FileInputStream(file)) {
            mockMultipartFile = new MockMultipartFile(
                    "file",
                    "words.xlsx",
                    "application/xxx", // mime type 을 잘못 지정
                    fis
            );
        }

        // when
        boolean result = excelUtils.isExcelFile(mockMultipartFile);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("엑셀의 첫 행을 포함하여 데이터를 추출할 수 있다.")
    @Test
    void readExcelWithFirstRow() throws IOException {
        // given
        File file = new ClassPathResource("static/words.xlsx").getFile();

        MockMultipartFile mockMultipartFile;
        try (FileInputStream fis = new FileInputStream(file)) {
            mockMultipartFile = new MockMultipartFile(
                    "file",
                    "words.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    fis
            );
        }

        // when
        List<List<String>> results = excelUtils.readExcel(mockMultipartFile, 200, true, true, true, "english", "korean");

        // then
        assertThat(results).hasSize(6)
                .flatExtracting(list -> list.get(0), list -> list.get(1))
                .containsExactly(
                        "english", "korean",
                        "apple", "사과",
                        "banana", "바나나",
                        "car", "자동차",
                        "dog", "개",
                        "eat", "먹다"
                );
    }

    @DisplayName("엑셀의 첫 행을 포함하지 않고 데이터를 추출할 수 있다.")
    @Test
    void readExcelWithoutFirstRow() throws IOException {
        // given
        File file = new ClassPathResource("static/words.xlsx").getFile();

        MockMultipartFile mockMultipartFile;
        try (FileInputStream fis = new FileInputStream(file)) {
            mockMultipartFile = new MockMultipartFile(
                    "file",
                    "words.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    fis
            );
        }

        // when
        List<List<String>> results = excelUtils.readExcel(mockMultipartFile, 200, true, false, true, "english", "korean");

        // then
        assertThat(results).hasSize(5)
                .flatExtracting(list -> list.get(0), list -> list.get(1))
                .containsExactly(
                        "apple", "사과",
                        "banana", "바나나",
                        "car", "자동차",
                        "dog", "개",
                        "eat", "먹다"
                );
    }

    @DisplayName("엑셀의 첫 행에 올바른 열 제목이 있는지를 검사할 수 있다.")
    @Test
    void inspectExcelHeader() throws IOException {
        // given
        File file = new ClassPathResource("static/words.xlsx").getFile();

        MockMultipartFile mockMultipartFile;
        try (FileInputStream fis = new FileInputStream(file)) {
            mockMultipartFile = new MockMultipartFile(
                    "file",
                    "words.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    fis
            );
        }

        // when
        // then
        assertThatThrownBy(() -> excelUtils.readExcel(mockMultipartFile, 200, true, true, true, "xxx", "yyy"))
                .isInstanceOf(UnmatchedExcelHeaderException.class);
    }

    @DisplayName("엑셀의 첫 행에 열 제목이 있는지를 검사하지 않을 수 있다.")
    @Test
    void notInspectExcelHeader() throws IOException {
        // given
        File file = new ClassPathResource("static/words.xlsx").getFile();

        MockMultipartFile mockMultipartFile;
        try (FileInputStream fis = new FileInputStream(file)) {
            mockMultipartFile = new MockMultipartFile(
                    "file",
                    "words.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    fis
            );
        }

        // when
        // then
        assertThatCode(() -> excelUtils.readExcel(mockMultipartFile, 200, true, true, false, "xxx", "yyy"))
                .doesNotThrowAnyException();
    }
}
