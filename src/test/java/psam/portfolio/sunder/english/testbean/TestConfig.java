package psam.portfolio.sunder.english.testbean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import psam.portfolio.sunder.english.global.aspect.trace.Trace;

@Slf4j
@TestConfiguration
public class TestConfig {

    @Bean
    @Trace(signature = false)
    public UniqueInfoContainer uniqueInfoContainer() {
        return StandaloneUniqueInfoContainer.builder()
                .numberOfCollection(30)
                .loginIdLen(8)
                .emailLen(8)
                .emailDomain("sunder.net")
                .academyNameMinLen(2)
                .academyNameMaxLen(8)
                .attendateIdLen(8)
                .build();
    }
}
