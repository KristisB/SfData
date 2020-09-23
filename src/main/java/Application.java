import Services.ReminderThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collections;

@Configuration
@EnableWebMvc
@ComponentScan("Services")
@EnableAutoConfiguration
public class Application {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        int port = getHerokuAssignedPort();
        System.out.println("port assigned "+ port);
        app.setDefaultProperties(Collections.singletonMap("server.port",port));
        app.run(args);
        runReminderThread();

    }
    public static final void runReminderThread(){
        Thread reminderSending = new Thread(new ReminderThread());
        reminderSending.start();
    }

    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 80; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}