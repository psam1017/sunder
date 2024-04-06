package psam.portfolio.sunder.english.global.pagination;

import com.querydsl.core.types.Order;

import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;

/**
 * SearchCond 는 Repository Tier 에서 요구할 유스케이스이다. 언제나 아래 4개의 값을 요구한다.
 * 따라서 모든 필드는 null 이 아니고, 기본값을 가져야 한다.
 * <br>
 * page 는 현재 페이지이다. -> 1
 * size 는 페이지마다 조회할 개시물 개수이며, 10 ~ 100 사이이다. -> 10
 * prop 은 정렬의 기준이다. -> id, createdDateTime 등
 * dir 은 정렬 방향이다. -> DESC
 */
public abstract class SearchCond {

    protected int page;
    protected int size;
    protected String prop;
    protected Order dir;

    public SearchCond() {
        this.page = 1;
        this.size = 10;
        this.prop = "";
        this.dir = DESC;
    }

    public SearchCond(Integer page, Integer size, String prop, String dir) {
        if (page == null || page < 0) {
            this.page = 1;
        } else {
            this.page = page;
        }
        if (size == null || size < 10) {
            this.size = 10;
        } else {
            this.size = Math.min(size, 100);
        }
        this.prop = prop == null ? "" : prop;
        this.dir = "asc".equalsIgnoreCase(dir) ? ASC : DESC;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public String getProp() {
        return prop;
    }

    public Order getDir() {
        return dir;
    }

    public long getOffset() {
        long offset = (long) (this.getPage() - 1) * this.getSize();
        return Math.max(offset, 0L);
    }

    public int getLimit() {
        return this.getSize();
    }
}
