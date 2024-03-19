package psam.portfolio.sunder.english.testbean;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.jdbc.core.JdbcTemplate;
import psam.portfolio.sunder.english.global.aspect.trace.Trace;

import javax.sql.DataSource;
import java.util.Set;

public class DataCleaner {

    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;

    public DataCleaner(DataSource dataSource, EntityManager entityManager) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.entityManager = entityManager;
    }

    @Trace
    public void cleanUp() {
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        for (EntityType<?> entity : entities) {
            Table table = entity.getJavaType().getAnnotation(Table.class);
            if (table != null) {
                jdbcTemplate.execute("TRUNCATE TABLE " + table.name());
            }
        }
    }
}
