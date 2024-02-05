package psam.portfolio.sunder.english.testbean;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public UniqueInfoContainer uniqueInfoContainer() {
        return StandaloneUniqueInfoContainer.builder()
                .numVal(30)
                .loginIdLen(8)
                .emailLen(8)
                .emailDomain("sunder.net")
                .academyNameMinLen(2)
                .academyNameMaxLen(8)
                .build();
    }
}
