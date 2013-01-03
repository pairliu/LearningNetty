package com.pairliu.netty.learning.pojotime;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class TimeServer {
    static final ChannelGroup allChannels = new DefaultChannelGroup("groupname");
    
    public static void main(String[] args) throws Exception {
        
        
        ChannelFactory fac = new NioServerSocketChannelFactory();
        
        ServerBootstrap boot = new ServerBootstrap(fac);
        
        boot.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() {
                return Channels.pipeline(new TimeServerHandler(), new TimeEncoder());
            }
        });
        
        Channel channel = boot.bind(new InetSocketAddress(8080));
        allChannels.add(channel);
        
//        Thread.sleep(5000);
//        
//        ChannelGroupFuture future = allChannels.close();
//        future.awaitUninterruptibly();
//        fac.releaseExternalResources();
    }

}
