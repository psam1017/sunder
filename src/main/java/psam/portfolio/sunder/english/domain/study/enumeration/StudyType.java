package psam.portfolio.sunder.english.domain.study.enumeration;

public enum StudyType {

    TRACING("따라쓰기"),
    SELECT("선택형"),
    WRITING("입력형");

    private final String value;

    StudyType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
