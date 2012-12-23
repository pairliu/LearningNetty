package com.pairliu.netty.learning.echo;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class EchoClient {
    private final String host;
    private final int port;
    private final int firstMessageSize;
    
    public EchoClient(final String host, final int port, final int firstMessageSize) {
        this.host = host;
        this.port = port;
        this.firstMessageSize = firstMessageSize;
    }
    
    public void run() {
        ClientBootstrap bootstrap = new ClientBootstrap( 
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));
        
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {                
                return Channels.pipeline(new EchoClientHandler(firstMessageSize));
            }
            
        });
        
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
        future.getChannel().getCloseFuture().awaitUninterruptibly();
        bootstrap.releaseExternalResources();
        
    }
    
    public static void main(String[] args) {
        
        EchoClient client = new EchoClient("localhost", 8080, 1024);
        client.run();
        
    }

}
