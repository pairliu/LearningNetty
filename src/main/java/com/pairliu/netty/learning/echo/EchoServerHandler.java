package com.pairliu.netty.learning.echo;

import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoServerHandler extends SimpleChannelUpstreamHandler {
    private static Logger LOG = LoggerFactory.getLogger(EchoServerHandler.class);
    
    private final AtomicLong transferredBytes = new AtomicLong();

    public long getTransferredBytes() {
        return transferredBytes.get();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        int bytes = ((ChannelBuffer) e.getMessage()).readableBytes();
        LOG.info("Bytes read: " + bytes);
        transferredBytes.addAndGet(((ChannelBuffer) e.getMessage()).readableBytes());
        e.getChannel().write(e.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }

}
