package com.zxd.test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;


/**
 * ChannelInboundHandlerAdapter类提供了可以覆盖各种事件的方法，我们只需要继承这个类，并覆盖其中的方法即可
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("channelActive................");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("channelInactive................");
    }

    /**
     * 覆盖事件处理的方法，每当从客户端接收到新数据时，都会调用此方法
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("show message...........");
        ByteBuf in = (ByteBuf) msg;
        try{
            System.out.println(in.toString(io.netty.util.CharsetUtil.UTF_8));
//            while (in.isReadable()){
//                System.out.println((char) in.readByte());
//                System.out.flush();
//            }
        }catch (Exception e){
            System.err.println(e.getMessage());
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 处理事件引发的异常，大多数情况下，应该记录捕获的异常并在此关闭其关联的同道
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
