package adivina_la_cancion.prototipo.adivina_la_cancion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1); // Solo necesitamos un hilo para esta tarea
        scheduler.setThreadNamePrefix("ScheduledTask-");
        scheduler.initialize();
        return scheduler;
    }
}
