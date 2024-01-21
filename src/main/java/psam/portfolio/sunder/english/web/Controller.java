package psam.portfolio.sunder.english.web;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class Controller {

    @GetMapping("/api/param")
    public String helloParam(@RequestParam("param") String param) {
        return "hello";
    }

    @GetMapping("/api/part")
    public String helloPart(@RequestPart("part") MultipartFile part) {
        return "hello";
    }

    @GetMapping("/api/servlet/bind")
    public String helloServletBind() throws ServletRequestBindingException {
        throw new ServletRequestBindingException("message");
    }
}
