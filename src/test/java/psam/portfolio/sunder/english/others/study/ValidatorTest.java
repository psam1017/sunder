package psam.portfolio.sunder.english.others.study;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import psam.portfolio.sunder.english.others.testconfig.TestConfig;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@Import(TestConfig.class)
@ActiveProfiles("test")
@SpringBootTest
public class ValidatorTest {

    @Autowired
    Validator validator;

    @Data
    @AllArgsConstructor
    private static class Target {

        @Pattern(regexp = "^\\S{1,127}$")
        private String field;
    }

    @DisplayName("null 은 ^\\S{1,127}$ 표현식을 통과할 수 있다.")
    @Test
    void regexNotEmpty1(){
        // given
        Target target = new Target(null);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "input");

        // when
        validator.validate(target, bindingResult);

        // then
        assertThat(bindingResult.hasErrors()).isFalse();
    }

    @DisplayName("빈 문자열은 ^\\S{1,127}$ 표현식을 통과할 수 없다.")
    @Test
    void regexNotEmpty2(){
        // given
        Target target = new Target("");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "input");

        // when
        validator.validate(target, bindingResult);

        // then
        assertThat(bindingResult.hasErrors()).isTrue();
    }

    @DisplayName("공백만으로 이루어진 문자열은 ^\\S{1,127}$ 표현식을 통과할 수 없다.")
    @Test
    void regexNotEmpty3(){
        // given
        Target target = new Target(" ");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "input");

        // when
        validator.validate(target, bindingResult);

        // then
        assertThat(bindingResult.hasErrors()).isTrue();
    }

    @DisplayName("공백을 포함하는 문자열은 ^\\S{1,127}$ 표현식을 통과할 수 없다.")
    @Test
    void regexNotEmpty4() {
        // given
        Target target = new Target(" a");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "input");

        // when
        validator.validate(target, bindingResult);

        // then
        assertThat(bindingResult.hasErrors()).isTrue();
    }

    @DisplayName("공백을 포함하지 않는 문자열은 ^\\S{1,127}$ 표현식을 통과할 수 있다.")
    @Test
    void regexNotEmpty5(){
        // given
        Target target = new Target("a");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "input");

        // when
        validator.validate(target, bindingResult);

        // then
        assertThat(bindingResult.hasErrors()).isFalse();
    }
}
