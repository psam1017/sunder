package psam.portfolio.sunder.english.global.slicing;

/**
 * 1. SlicingSearchCond 는 그 특성상 마지막으로 조회한 sequence 를 기억해야 무한 스크롤이 가능하다.
 * 예를 들어, 가장 최신이었던 sequence 가 10 인 상태에서 sequence = 11 인 데이터가 추가되면 다음 슬라이싱에서 데이터가 중복되어 전달된다.
 * 따라서 마지막 sequence 와 비교하여 데이터를 가져와야 게시물 추가에 의한 중복을 방지할 수 있다.
 * 모든 필드는 null 이 아니고, 기본값을 가져야 한다.
 * 2. 슬라이싱은 기본적으로 요청한 size + 1 개를 조회하여 다음 게시물이 있는지를 확인한다.
 * <br>
 * size 는 페이지마다 조회할 개시물 개수이며, 10 ~ 100 사이이다. -> 10
 * lastSequence 는 마지막으로 조회한 sequence 이다. -> 1
 */
public abstract class SlicingSearchCond {

    protected int size;
    protected long lastSequence;

    public SlicingSearchCond(Integer size, Long lastSequence) {
        this.size = size == null || size < 10 ? 10 : Math.min(size, 100);
        this.lastSequence = lastSequence == null ? Long.MAX_VALUE : lastSequence;
    }

    public int getSize() {
        return size;
    }

    public Long getLastSequence() {
        return lastSequence;
    }

    public long getLimit() {
        return this.getSize() + 1;
    }
}
