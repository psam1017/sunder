package psam.portfolio.sunder.english.domain.study.model.enumeration;

/**
 * 따라쓰기,
 * 선택형,
 * 입력형(자가채점)
 */
public enum StudyClassification {

    EXAM("시험"),
    PRACTICE("연습");

    private final String value;

    StudyClassification(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
