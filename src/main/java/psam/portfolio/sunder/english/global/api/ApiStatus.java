package psam.portfolio.sunder.english.global.api;

import org.springframework.util.ObjectUtils;

/**
 * 0xx : Business - 비즈니스 로직 수행 도중에 발생시킨 예외에 의한 코드
 * 1xx : Informational - 요청을 여전히 처리 중
 * 2xx : Successful - 요청을 정상 처리
 * 3xx : Redirection - 요청을 완료하려면 추가 행동이 필요
 * 4xx : Client Error - 클라이언트 오류, 잘못된 문법
 * 5xx : Server Error - 서버 오류
 */
public enum ApiStatus {

    DUPLICATE_KEY("011", "DuplicateKey"),
    NO_SUCH_ELEMENT("012", "NoSuchElement"),
    ACCESS_DENIAL("013", "AccessDenial"),
    ILLEGAL_DATA("031", "IllegalData"),
    ILLEGAL_STATUS("032", "IllegalStatus"),
    TIME_OUT("041", "TimeOut"),

    OK("200", "OK"),
    CREATED("201", "Created"),
    ACCEPTED("202", "Accepted"),
    NO_CONTENT("204", "NoContent"),
    /**
     * redirect with changing method to GET. respond with Location Header.
     */
    SEE_OTHER("303", "SeeOther"),
    /**
     * requested resources is not modified. so, cache data is still valid. must not send body, only headers.
     * e.g. Cache-Control, Pragma, Expires, ETag, Last-Modified
     */
    NOT_MODIFIED("304", "NotModified"),
    /**
     * api was changed, but not sure. so, it can be rolled back. respond with Location Header.
     */
    TEMPORARY_REDIRECT("307", "TemporaryRedirect"),
    /**
     * api was changed permanently. so, it wouldn't be rolled back. respond with Location Header.
     */
    PERMANENT_REDIRECT("308", "PermanentRedirect"),
    BAD_REQUEST("400", "BadRequest"),
    UNAUTHORIZED("401", "Unauthorized"),
    FORBIDDEN("403", "Forbidden"),
    NOT_FOUND("404", "NotFound"),
    /**
     * wrong method, perhaps. respond with Allow Header.
     */
    METHOD_NOT_ALLOWED("405", "MethodNotAllowed"),
    /**
     * cannot negotiate content with the client.
     * let the client know the available representations of the resources for the client to choose among them.
     * also, define "produces" in @RequestMapping.
     */
    NOT_ACCEPTABLE("406", "NotAcceptable"),
    /**
     * the server refuses to accept the request because the payload format is in an unsupported format.
     * let the client know the supported media types.
     * also, define "consumes" in @RequestMapping
     */
    UNSUPPORTED_MEDIA_TYPE("415", "UnsupportedMediaType"),
    PRECONDITION_REQUIRED("428", "PreconditionRequired"),
    TOO_MANY_REQUESTS("429", "TooManyRequests"),

    INTERNAL_SERVER_ERROR("500", "InternalServerError"),
    BAD_GATEWAY("502", "BadGateway"),
    SERVICE_UNAVAILABLE("503", "ServiceUnavailable");

    private final String code;
    private final String message;

    ApiStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }

    public ApiStatus of(String code) {
        for (ApiStatus status : ApiStatus.values()) {
            if (ObjectUtils.nullSafeEquals(status.code(), code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + code + "]");
    }
}
