package com.example.client.test.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class TimeServer {

    public void bind(int port) {
        //NioEventLoopGroup是个线程组，它包含了一组NIO线程，专门用于网络时间的处理，实际上它们就是Reactor线程组。这里创建两个的原因是一个用于服务端接受客户端的链接，另一个用于进行SocketChannel的网络读写
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //它是Netty用于启动NIO服务端的辅助启动类，目的是降低服务端开发的复杂度
            ServerBootstrap b = new ServerBootstrap();
            /**
             * 设置创建的Channel为NioServerChannel，它的功能对于与JDK NIO类库中的ServerSocketChannel类。
             * 然后配置NIOServerSocketChannel的TCP参数，此处将它的backlog设置为1024，最后绑定IO事件的处理类ChildChannelHandler，
             * 它的作用类似于Reactor模式中的Handler类，主要用于处理网络IO事件，例如记录日志，对消息进行编码解码等
             *
             */
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //.localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                            socketChannel.pipeline().addLast(new TimeServerHandler());
                        }
                    }).childOption(ChannelOption.SO_KEEPALIVE, true);
            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();
            //等待服务器监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) {
        int port = 9000;
        new TimeServer().bind(port);
    }




}
