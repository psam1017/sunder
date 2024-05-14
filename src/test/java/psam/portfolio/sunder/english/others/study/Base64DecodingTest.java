package psam.portfolio.sunder.english.others.study;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Disabled
public class Base64DecodingTest {

    @SuppressWarnings("SpellCheckingInspection")
    @DisplayName("Base64 로 인코딩한 문자열을 다시 디코딩하고 json 으로 변환할 수 있다.")
    @Test
    public void decodeStringIntoJson() throws IOException {
        // given
        String encodedString = "eyJib29rIjp7ImlkIjoiMWFlMWVlMTItZGU5MS00MTBjLTg5MzktYjMxOTU5OWZjMmQ3IiwicHVibGlzaGVyIjoicHVibGlzaGVyIiwiYm9va05hbWUiOiJib29rTmFtZSIsImNoYXB0ZXIiOiJjaGFwdGVyIiwic3ViamVjdCI6InN1YmplY3QiLCJhY2FkZW15SWQiOiJmZWIxNTlmMC0wM2Q3LTQ3Y2ItYmEzYS0wNDUzNzg4MGQxODciLCJvcGVuVG9QdWJsaWMiOmZhbHNlLCJjcmVhdGVkRGF0ZVRpbWUiOiIyMDI0LTA1LTE0VDIxOjA1OjUxIiwibW9kaWZpZWREYXRlVGltZSI6IjIwMjQtMDUtMTRUMjE6MDU6NTEiLCJjcmVhdGVkQnkiOm51bGwsIm1vZGlmaWVkQnkiOm51bGx9LCJ3b3JkcyI6W3siaWQiOjQxOSwia29yZWFuIjoi7IKs6rO8IiwiZW5nbGlzaCI6ImFwcGxlIn0seyJpZCI6NDIwLCJrb3JlYW4iOiLrsJTrgpjrgpgiLCJlbmdsaXNoIjoiYmFuYW5hIn0seyJpZCI6NDIxLCJrb3JlYW4iOiLssrTrpqwiLCJlbmdsaXNoIjoiY2hlcnJ5In1dfQ==";

        // when
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String decodedJsonString = new String(decodedBytes, StandardCharsets.UTF_8);

        // then
        System.out.println(decodedJsonString);
    }
}
