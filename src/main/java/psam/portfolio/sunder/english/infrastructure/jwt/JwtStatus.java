package psam.portfolio.sunder.english.infrastructure.jwt;

public enum JwtStatus {

    BLANK,
    ILLEGAL_SIGNATURE,
    EXPIRED,
    MALFORMED,
    UNSUPPORTED,
    FORBIDDEN,
    ILLEGAL_IP_ACCESS
}
