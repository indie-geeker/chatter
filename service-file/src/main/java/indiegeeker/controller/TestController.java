package indiegeeker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Desc:
 * Author: wen
 * Date: 2025/6/22
 **/
@RestController
@RequestMapping("file")
public class TestController {

    @GetMapping("hello")
    public Object hello(){
        return "hello file";
    }
}
