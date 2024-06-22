package psam.portfolio.sunder.english.domain.log;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.RandomAccessFile;

@RestController
public class LogController {

    @Value("${sunder.log.path}")
    private String LOG_FILE_PATH;

    @Secured("ROLE_ADMIN")
    @ResponseBody
    @GetMapping("/api/log")
    public String getLog(@RequestParam(defaultValue = "<br>") String lineBreak,
                         @RequestParam(defaultValue = "200") Integer maxLogLines
    ) {
        try (RandomAccessFile file = new RandomAccessFile(LOG_FILE_PATH, "r")) {
            long fileLength = file.length();
            long pointer = fileLength - 1;
            int lineCount = 0;
            StringBuilder sb = new StringBuilder();

            file.seek(pointer);
            for (long i = pointer; i >= 0; i--) {
                file.seek(i);
                char c = (char) file.read();
                if (c == '\n') {
                    lineCount++;
                    if (lineCount > maxLogLines) {
                        break;
                    }
                }
                sb.append(c);
            }

            // Reverse the content since we read from the end
            if (StringUtils.hasText(lineBreak)) {
                return sb.reverse().toString().replace("\n", lineBreak);
            } else {
                return sb.reverse().toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR OCCURRED WHILE READING LOG FILE";
        }
    }
}
