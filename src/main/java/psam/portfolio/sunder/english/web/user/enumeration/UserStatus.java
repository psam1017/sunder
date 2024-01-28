package psam.portfolio.sunder.english.web.user.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserStatus {

    PENDING,
    TRIAL,
    TRIAL_END,
    ACTIVE,
    FORBIDDEN,
    WITHDRAWN;

    @JsonCreator
    public UserStatus of(String str) {
        return UserStatus.valueOf(str);
    }
}
