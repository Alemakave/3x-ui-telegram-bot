package ru.alemakave.xuitelegrambot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

@SpringBootApplication
@EnableAsync
@Slf4j
public class Application {
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Application.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
		log.info("Started 3x-ui Telegram Bot v" + BuildInfo.BUILD_VERSION);
    }

	public Application() {
		SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
	}
}
