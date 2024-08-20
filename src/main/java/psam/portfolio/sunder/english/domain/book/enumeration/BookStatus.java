package psam.portfolio.sunder.english.domain.book.enumeration;

public enum BookStatus {

    CREATED("생성"),
    DELETED("삭제");

    private final String value;

    BookStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
