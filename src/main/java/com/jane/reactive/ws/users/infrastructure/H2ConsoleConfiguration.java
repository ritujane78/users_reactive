package com.jane.reactive.ws.users.infrastructure;

import org.h2.tools.Server;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.sql.SQLException;

@Configuration
@Profile("!prod & !production")
public class H2ConsoleConfiguration {
    private Server webServer;

    @EventListener(ApplicationReadyEvent.class)
    public void start() throws SQLException {
        String WEB_PORT= "8082";
        this.webServer = Server.createWebServer("-webPort", WEB_PORT).start();
    }

    @EventListener(ContextClosedEvent.class)
    public void stop() throws SQLException {
        this.webServer.stop();
    }
}
