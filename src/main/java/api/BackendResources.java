package api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/misc")
public class BackendResources {

    @GetMapping("")
    public String helloGradle() {
        return "Hello Gradle!";
    }

}
