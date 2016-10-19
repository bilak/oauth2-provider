package com.github.bilak.oauth2.provider.configuration;

import org.h2.tools.Server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.SQLException;

/**
 * Created by lvasek on 16/10/2016.
 */
public class H2ServletListener implements ServletContextListener {

	private String serverPort;
	private Server server;

	public H2ServletListener(String serverPort) {
		this.serverPort = serverPort;
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		try {
			server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", serverPort).start();
		} catch (SQLException e) {
			throw new RuntimeException("Unable to initialize H2 server on port " + serverPort);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		if (server != null && server.isRunning(true))
			server.stop();
	}
}
