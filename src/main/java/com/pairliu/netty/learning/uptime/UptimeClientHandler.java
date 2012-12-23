package com.pairliu.netty.learning.uptime;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.timeout.ReadTimeoutException;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

/**
 * Keep reconnecting to the server while printing out the current uptime and
 * connection attempt status.
 */
public class UptimeClientHandler extends SimpleChannelUpstreamHandler {

    final ClientBootstrap bootstrap;
    private final Timer timer;
    private long startTime = -1;
    private final AtomicLong reconnectTimes = new AtomicLong();
    static final int RECONNECT_LIMIT = 5;

    public UptimeClientHandler(ClientBootstrap bootstrap, Timer timer) {
        this.bootstrap = bootstrap;
        this.timer = timer;
    }

    InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) bootstrap.getOption("remoteAddress");
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        println("Disconnected from: " + getRemoteAddress());
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
        //The so-called "reliable reconnect" just means reconnect in "channelClosed"
        //Maybe delaying several seconds makes it more reliable
        
        if (reconnectTimes.getAndIncrement() > 5) {
            println("Too many reconnection. Just stop...");
          //The following method is not allowed to call here: java.lang.IllegalStateException: Must not be called from a I/O-Thread to prevent deadlocks!
//            bootstrap.releaseExternalResources(); 
        } else {
            println("Sleeping for: " + UptimeClient.RECONNECT_DELAY + 's');
            timer.newTimeout(new TimerTask() {
                public void run(Timeout timeout) throws Exception {
                    println("Reconnecting to: " + getRemoteAddress());
                    bootstrap.connect();
                }
            }, UptimeClient.RECONNECT_DELAY, TimeUnit.SECONDS);
        }
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        if (startTime < 0) {
            startTime = System.currentTimeMillis();
        }

        println("Connected to: " + getRemoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        Throwable cause = e.getCause();
        if (cause instanceof ConnectException) {
            startTime = -1;
            println("Failed to connect: " + cause.getMessage());
        }
        if (cause instanceof ReadTimeoutException) {
            // The connection was OK but there was no traffic for last period.
            println("Disconnecting due to no inbound traffic");
        } else {
            cause.printStackTrace();
        }
        ctx.getChannel().close();
    }

    void println(String msg) {
        if (startTime < 0) {
            System.err.format("[SERVER IS DOWN] %s%n", msg);
        } else {
            System.err.format("[UPTIME: %5ds] %s%n", (System.currentTimeMillis() - startTime) / 1000, msg);
        }
    }

}
