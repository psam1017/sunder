package psam.portfolio.sunder.english.domain.book.enumeration;

public enum WordStatus {

    CREATED("생성"),
    DELETED("삭제");

    private final String value;

    WordStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
