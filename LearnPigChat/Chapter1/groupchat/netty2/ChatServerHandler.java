package org.example.netty.groupchat.netty2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @Author: Usher
 * @Description:
 * 回调处理类,继承SimpleChannelInboundHandler处理出站入站数据，模板设计模式，让主要的处理逻辑保持不变，让变化的步骤通过接口实现来完成
 */
public class ChatServerHandler extends SimpleChannelInboundHandler <String>{


    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    /**
     * 当有客户端连接时，handlerAdded会执行,就把该客户端的通道记录下来，加入队列
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();//获得客户端通道
        //通知其他客户端有新人进入
        for (Channel channel : channels){
            if (channel != inComing)
            {

                channel.writeAndFlush("[welcome: " + inComing.remoteAddress() + "]  come in chat room\n");
            }
        }

        channels.add(inComing);//加入队列
    }

    /**
     * 断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel outComing = ctx.channel();//获得客户端通道
        //通知其他客户端有人离开
        for (Channel channel : channels){
            if (channel != outComing)
            {
                channel.writeAndFlush("[byebye: ]" + outComing.remoteAddress() + " leave chat room\n");
            }
        }

        channels.remove(outComing);
    }

    /**
     * 每当从客户端有消息写入时
     * @param channelHandlerContext
     * @param s
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        Channel inComing = channelHandlerContext.channel();
      //  System.out.println("---------"+s);
        for (Channel channel : channels){
            if (channel != inComing){
                System.out.println("++++++++++++++++             "+s);
                channel.writeAndFlush("[user:" + inComing.remoteAddress() + " say:]" + s + "\n");
            }else {
                channel.writeAndFlush("[I say:]" + s + "\n");
            }
        }
    }

    /**
     * 当服务器监听到客户端活动时
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();
        System.out.println("[" + inComing.remoteAddress() + "]------ online");
    }

    /**
     * 离线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();
        System.out.println("[" + inComing.remoteAddress() + "]------ offline");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel inComing = ctx.channel();
        System.out.println(inComing.remoteAddress() + "  communication error  ");
        ctx.close();
    }
}
