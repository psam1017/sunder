package psam.portfolio.sunder.english.infrastructure.excel;

public enum ExcelType {

    XLS("xls", "application/vnd.ms-excel"),
    XLXS("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final String extension;
    private final String mimeType;

    ExcelType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String extension() {
        return extension;
    }

    public String mimeType() {
        return mimeType;
    }
}
