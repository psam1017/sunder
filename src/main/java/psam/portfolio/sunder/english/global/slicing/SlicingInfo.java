package psam.portfolio.sunder.english.global.slicing;

public class SlicingInfo {

    private int size;
    private long lastSequence;
    private boolean hasNext;

    public SlicingInfo(int size, long lastSequence, boolean hasNext) {
        this.size = size;
        this.lastSequence = lastSequence;
        this.hasNext = hasNext;
    }

    public int getSize() {
        return size;
    }

    public long getLastSequence() {
        return lastSequence;
    }

    public boolean hasNext() {
        return hasNext;
    }
}
