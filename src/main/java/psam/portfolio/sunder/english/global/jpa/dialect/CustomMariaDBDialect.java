package psam.portfolio.sunder.english.global.jpa.dialect;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.MariaDBDialect;
import org.hibernate.type.BasicType;
import org.hibernate.type.StandardBasicTypes;

// application.properties 에 `spring.jpa.properties.hibernate.dialect=psam.portfolio.sunder.english.global.jpa.dialect.CustomMariaDBDialect` 추가.
@SuppressWarnings("unused")
public class CustomMariaDBDialect extends MariaDBDialect {

    // MariaDB my.ini 에 `innodb_ft_min_token_size=1` 추가.
    @Override
    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
        super.initializeFunctionRegistry(functionContributions);
        BasicType<Boolean> booleanBasicType = functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(StandardBasicTypes.BOOLEAN);

        functionContributions.getFunctionRegistry().registerPattern(
                "match_against",
                "match(?1) against(?2 in boolean mode)",
                booleanBasicType
        );
    }
}
