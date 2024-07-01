package psam.portfolio.sunder.english.domain.academy.model.enumeration;

public enum AcademyStatus {

    PENDING("대기"),
    VERIFIED("인증"),
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
