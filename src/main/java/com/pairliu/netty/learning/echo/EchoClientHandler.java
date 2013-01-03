package com.pairliu.netty.learning.echo;

import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoClientHandler extends SimpleChannelUpstreamHandler {
    private static Logger LOG = LoggerFactory.getLogger(EchoServerHandler.class);
    
    private final ChannelBuffer message;
    private final AtomicLong transferredBytes = new AtomicLong();
    
    public EchoClientHandler(final int messageSize) {
        message = ChannelBuffers.buffer(messageSize);
        for(int i = 0; i < message.capacity(); i++) {
            message.writeByte(i);
        }
    }
    
    public long getTransferredBytes() {
        return transferredBytes.get();
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {        
        ChannelBuffer buffer = (ChannelBuffer)(e.getMessage());
        LOG.info("Echo back: " + buffer);
        transferredBytes.addAndGet(buffer.readableBytes());
        e.getChannel().write(buffer);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        e.getChannel().write(message);
    }

}
