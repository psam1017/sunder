package psam.portfolio.sunder.english.domain.study.enumeration;

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
