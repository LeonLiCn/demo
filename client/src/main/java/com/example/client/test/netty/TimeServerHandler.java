package com.example.client.test.netty;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import java.util.Date;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    private int loss_connect_time = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        //将msg转换 成Netty的ByteBuf对象。ByteBuf类似于JDK中的java.nio.ByteBuffer对象，不过它提供了更加强大和灵活的功能
        ByteBuf buf = (ByteBuf) msg;
        //通过ByteBuf的readableBytes方法可以获取缓冲区可读的字节数，根据可读的字节数创建byte数组
        byte[] bytes = new byte[buf.readableBytes()];
        //通过ByteBuf的readBytes方法将缓冲区中的字节数组复制到新建的byte数组中
        buf.readBytes(bytes);
        //最后通过new String构造函数获取请求消息
        String body = new String(bytes, CharsetUtil.UTF_8);
        System.out.println("服务器收到命令：" + body);
        //这是对请求消息进行判断，如果是"QUERY TIME ORDER"则创建应答消息，通过ChannelHandlerContext的write方法异步发送应答消息给客户端
        String time = "QUERY TIME ORDER".equalsIgnoreCase(body) ? "已接受到消息，时间：" + new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        ByteBuf resp = Unpooled.copiedBuffer(time.getBytes());
        ctx.write(resp);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
        // channelRead()执行完成后，关闭channel连接
    //    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                loss_connect_time++;
                System.out.println("5S 没有接受到客户端请求了！");
                if (loss_connect_time > 2) {
                    System.out.println("关闭这个不活跃的channel");
                    ctx.channel().close();
                }
            }

        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


}
