package psam.portfolio.sunder.english.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import psam.portfolio.sunder.english.global.aspect.trace.TraceAspect;

@Configuration
public class AspectConfig {

    @Bean
    public TraceAspect beanCreationTimeAspect() {
        return new TraceAspect();
    }
}
