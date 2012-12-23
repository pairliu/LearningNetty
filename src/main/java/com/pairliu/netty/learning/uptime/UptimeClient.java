package com.pairliu.netty.learning.uptime;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

/**
 * Connects to a server periodically to measure and print the uptime of the
 * server. This example demonstrates how to implement reliable reconnection
 * mechanism in Netty.
 */
public class UptimeClient {
    static final int RECONNECT_DELAY = 2;

    // Reconnect when the server sends nothing for 10 seconds.
    private static final int READ_TIMEOUT = 3;

    private final String host;
    private final int port;

    public UptimeClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        // Initialize the timer that schedules subsequent reconnection attempts.
        final Timer timer = new HashedWheelTimer();

        // Configure the client.
        final ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        // Configure the pipeline factory.
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

            private final ChannelHandler timeoutHandler = new ReadTimeoutHandler(timer, READ_TIMEOUT);
            private final ChannelHandler uptimeHandler = new UptimeClientHandler(bootstrap, timer);

            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(timeoutHandler, uptimeHandler);
            }
        });

        bootstrap.setOption("remoteAddress", new InetSocketAddress(host, port));

        // Initiate the first connection attempt - the rest is handled by
        // UptimeClientHandler.
        bootstrap.connect();
    }

    public static void main(String[] args) throws Exception {
        new UptimeClient("www.google.com.hk", 80).run();
    }
}