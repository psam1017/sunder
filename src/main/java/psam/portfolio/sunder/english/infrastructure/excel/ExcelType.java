package psam.portfolio.sunder.english.infrastructure.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Objects;

@SuppressWarnings("SpellCheckingInspection")
@Slf4j
public enum ExcelType {

    XLS("xls", new String[]{"application/vnd.ms-excel", "application/msexcel"}),
    XLSX("xlsx", new String[]{"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"});

    private final String extension;
    private final String[] mimeTypes;

    ExcelType(String extension, String[] mimeTypes) {
        this.extension = extension;
        this.mimeTypes = mimeTypes;
    }

    public String extension() {
        return extension;
    }

    public String[] mimeTypes() {
        return mimeTypes;
    }

    public static boolean isExcelFile(String extension, String mimeType) {
        if (StringUtils.hasText(extension) && StringUtils.hasText(mimeType)) {
            for (ExcelType et : ExcelType.values()) {
                if (Objects.equals(et.extension(), extension)) {
                    mimeType = mimeType.toLowerCase();
                    for (String mt : et.mimeTypes()) {
                        if (mt.contains(mimeType)) {
                            return true;
                        }
                    }
                }
            }
        }
        log.info("Illegal File submitted. extension = {}, mimeType = {}", extension, mimeType);
        return false;
    }
}
