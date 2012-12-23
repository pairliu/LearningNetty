package com.pairliu.netty.learning.pojotime;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class TimeClient {
    public static void main(String[] args) {
        ChannelFactory fac = new NioClientSocketChannelFactory();
        
        ClientBootstrap boot = new ClientBootstrap(fac);
        
        boot.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(new TimeDecoder(), new TimeClientHandler());
            }
            
        });
        
        ChannelFuture future = boot.connect(new InetSocketAddress("localhost", 8080));
        future.awaitUninterruptibly(); //wait for everything is done
        
        //Now it is down. Ready to clean up and close.
        if(!future.isSuccess()) {
            future.getCause().printStackTrace();
        }
        future.getChannel().getCloseFuture().awaitUninterruptibly();
        fac.releaseExternalResources(); //ChannelFactory and Bootstrap both has this method. Using Bootstrap is better.
    }

}
