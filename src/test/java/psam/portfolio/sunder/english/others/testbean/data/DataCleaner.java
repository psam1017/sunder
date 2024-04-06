package psam.portfolio.sunder.english.others.testbean.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import psam.portfolio.sunder.english.global.aspect.trace.Trace;

import javax.sql.DataSource;
import java.util.Set;

@Component
public class DataCleaner {

    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;

    public DataCleaner(DataSource dataSource, EntityManager entityManager) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.entityManager = entityManager;
    }

    @Trace(signature = false)
    public void cleanUp() {
        Set<EntityType<?>> entities = entityManager.getMetamodel().getEntities();
        for (EntityType<?> entity : entities) {
            Table table = entity.getJavaType().getAnnotation(Table.class);
            if (table != null) {
                jdbcTemplate.execute("truncate table " + table.name());
            }
        }
    }
}
