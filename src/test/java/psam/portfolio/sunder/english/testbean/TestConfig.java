package psam.portfolio.sunder.english.testbean;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public UniqueInfoContainer uniqueInfoContainer() {
        return UniqueInfoContainer.builder()
                .numVal(100)
                .userNameLen(8)
                .userEmailLen(8)
                .emailDom("sunder.net")
                .academyNameMinLen(2)
                .academyNameMaxLen(8)
                .build();
    }
}
