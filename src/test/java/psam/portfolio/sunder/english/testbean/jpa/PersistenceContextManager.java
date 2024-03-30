package psam.portfolio.sunder.english.testbean.jpa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Component
public class PersistenceContextManager {

    private final EntityManager em;

    public <T> T refreshAnd(Supplier<T> action) {
        em.flush();
        em.clear();
        System.out.println("\n#============================== Flush and Clear. Action Start. ==============================\n");
        T result = action.get();
        em.flush();
        em.clear();
        System.out.println("\n#============================== Flush and Clear. Action Finished. ==============================\n");
        return result;
    }

    public void refreshAnd(Runnable action) {
        em.flush();
        em.clear();
        System.out.println("\n#============================== Flush and Clear. Action Start. ==============================\n");
        action.run();
        em.flush();
        em.clear();
        System.out.println("\n#============================== Flush and Clear. Action Finished. ==============================\n");
    }

    public void refresh() {
        em.flush();
        em.clear();
        System.out.println("\n#============================== Flush and Clear. Refresh Done. ==============================\n");
    }
}
