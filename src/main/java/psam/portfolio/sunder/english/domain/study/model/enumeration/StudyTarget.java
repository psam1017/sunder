package psam.portfolio.sunder.english.domain.study.model.enumeration;

public enum StudyTarget {

    KOREAN("한국어"),
    ENGLISH("영어");

    private final String value;

    StudyTarget(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
