package psam.portfolio.sunder.english.domain.user.model.enumeration;

public enum RoleName { // ROLE_XXX(spring security 규칙)

    ROLE_ADMIN("관리자"),
    ROLE_DIRECTOR("학원장"),
    ROLE_TEACHER("선생"),
    ROLE_STUDENT("학생");

    private final String value;

    RoleName(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
