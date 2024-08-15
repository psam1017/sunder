package psam.portfolio.sunder.english.global.slicing;

import lombok.Getter;

@Getter
public class SlicingInfo {

    private int size;
    private long lastSequence;
    private boolean hasNext;

    public SlicingInfo(int size, long lastSequence, boolean hasNext) {
        this.size = size;
        this.lastSequence = lastSequence;
        this.hasNext = hasNext;
    }

    public boolean hasNext() {
        return hasNext;
    }
}
