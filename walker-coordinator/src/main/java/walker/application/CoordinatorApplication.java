package walker.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.event.ContextClosedEvent;

import lombok.extern.slf4j.Slf4j;
import walker.application.coordinator.config.CoordinatorConfiguration;
import walker.application.infrastructure.hook.GracefulShutdownTomcat;

@SpringBootApplication
@ImportResource(locations = {"classpath:/spring/coordinator-spring.xml"})
@Import(CoordinatorConfiguration.class)
@Slf4j
public class CoordinatorApplication {

    @Autowired
    private GracefulShutdownTomcat gracefulShutdownTomcat;

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addConnectorCustomizers(gracefulShutdownTomcat);
        return tomcat;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CoordinatorApplication.class, args);
        context.addApplicationListener(new ApplicationListener<ContextClosedEvent>() {
            @Override
            public void onApplicationEvent(ContextClosedEvent event) {
                log.info("[[Coordinator StartED UP]]");
            }
        });
    }
}
