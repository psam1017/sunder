package psam.portfolio.sunder.english.others.study;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StringReplaceAllTest {

    @DisplayName("정규표현식을 사용하여 문자열의 연속된 하나 이상의 공백을 하나의 공백으로 바꿀 수 있다.")
    @Test
    public void replaceAllWhiteSpace() {
        // given
        String text = "H e  l   l    o     W    o   r  l d!";

        // when
        String result = text.replaceAll("\\s+", " ");

        // then
        Assertions.assertThat(result).isEqualTo("H e l l o W o r l d!");
    }
}
