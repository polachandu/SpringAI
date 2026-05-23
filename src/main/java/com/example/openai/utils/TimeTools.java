package com.example.openai.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.ZoneId;

@Component
@Slf4j
public class TimeTools {

    @Tool(name = "getCurrentLocalTime", description = "Get the current time in the users's timezone")
    String getCurrentLocalTime() {
        log.info("Returning the current time in the user's timezone");
        return LocalTime.now().toString();
    }

    @Tool(name = "getCurrentTme", description = "Get the current time in the specified timezone")
    String getCurrentTime(@ToolParam(description = "Value representing the time zone") String timezone) {
        log.info("Returning the current time in the timezone");
        return LocalTime.now(ZoneId.of(timezone)).toString();
    }
}
