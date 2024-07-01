package psam.portfolio.sunder.english.global.slicing;

public class SlicingInfo {

    protected int size;
    protected long lastSequence;
    protected boolean hasNext;

    public SlicingInfo(int size, long lastSequence, boolean hasNext) {
        this.size = size;
        this.lastSequence = lastSequence;
        this.hasNext = hasNext;
    }
}
