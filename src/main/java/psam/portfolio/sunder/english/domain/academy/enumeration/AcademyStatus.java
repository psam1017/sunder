package psam.portfolio.sunder.english.domain.academy.enumeration;

public enum AcademyStatus {

    PENDING("대기중"),
    VERIFIED("인증완료"),
    FORBIDDEN("차단"),
    WITHDRAWN("탈퇴");

    private final String value;

    AcademyStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
