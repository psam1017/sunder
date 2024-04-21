package psam.portfolio.sunder.english.domain.user.model.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

public enum UserStatus {

    PENDING,
    TRIAL,
    TRIAL_END,
    ACTIVE,
    FORBIDDEN,
    WITHDRAWN;

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
