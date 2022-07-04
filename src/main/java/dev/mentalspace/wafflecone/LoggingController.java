package dev.mentalspace.wafflecone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoggingController {
    public static Logger logger = LoggerFactory.getLogger(LoggingController.class);
}