package psam.portfolio.sunder.english.domain.study.model.enumeration;

public enum StudyStatus {

    ASSIGNED("숙제"), // 선생님이 학생에게 Study 를 할당
    STARTED("시작"), // 학생이 직접 Study 를 시작
    SUBMITTED("제출"), // 학생이 Study 를 완료
    DELETED("삭제"); // 선생님이 Study 를 삭제

    private final String value;

    StudyStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
