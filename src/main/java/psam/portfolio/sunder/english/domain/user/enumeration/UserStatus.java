package psam.portfolio.sunder.english.domain.user.model.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

public enum UserStatus {

    PENDING("대기"),
    TRIAL("체험"),
    TRIAL_END("체험종료"),
    ACTIVE("일반회원"),
    FORBIDDEN("차단"),
    WITHDRAWN("탈퇴");

    private final String value;

    UserStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @JsonCreator
    public static UserStatus ofNullable(String str) {
        for (UserStatus us : UserStatus.values()) {
            if (Objects.equals(us.name(), str)) {
                return us;
            }
        }
        return null;
    }
}
