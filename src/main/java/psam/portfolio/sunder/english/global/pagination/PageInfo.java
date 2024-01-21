package psam.portfolio.sunder.english.global.pagination;

import lombok.Getter;

@Getter
public class PageInfo {

    protected final int page;
    protected final int size;
    protected final long total;
    protected final int lastPage;
    protected final int start;
    protected final int end;
    protected final boolean hasPrev;
    protected final boolean hasNext;

    public PageInfo(int page, int size, long total, int pageSetAmount) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.lastPage = (int) Math.ceil((double) total / (double) size);
        if (pageSetAmount == 1) {
            this.start = page;
            this.end = page;
        } else {
            this.start = ((page - 1) / size) * size + 1;
            this.end = Math.min(start + pageSetAmount - 1, lastPage);
        }
        this.hasPrev = start != 1;
        this.hasNext = end < lastPage;
    }
}
