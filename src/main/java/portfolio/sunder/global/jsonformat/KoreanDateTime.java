package portfolio.sunder.global.jsonformat;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@JacksonAnnotationsInside
@Retention(RetentionPolicy.RUNTIME)
@JsonFormat(timezone = "Asia/Seoul", pattern = "yyyy-MM-dd'T'HH:mm:ss")
public @interface KoreanDateTime {
}
