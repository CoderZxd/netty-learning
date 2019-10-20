package com.zxd.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DiscardServer {

    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run() throws Exception{
        // NioEventLoopGroup是一个处理I/O操作的多线程事件循环
        // 使用了多少个线程以及它们如何映射到创建的Channels取决于EventLoopGroup实现，甚至可以通过构造函数进行配置
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            // 设置服务器的帮助程序类
            ServerBootstrap bootstrap = new ServerBootstrap();
            // 将创建好的两个group添加到serverBootStrap中
            bootstrap.group(bossGroup,workerGroup)
                    // 指定使用 NioServerSocketChannel 来接受传入连接
                    // childHandler指定处理程序，ChannelInitializer旨在帮助用户配置新的channel
                    .channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //将我们定义好的DiscardServerHandler丢在管道中
                            socketChannel.pipeline().addLast(new DiscardServerHandler());
                        }
                    })
                    // 这里可以设置特定的channel参数，为了接受传入的连接
                    .option(ChannelOption.SO_BACKLOG,128)
                    // 追加操作项，因为我们正在编写TCP/IP服务器，因此我们可以设置套接字选项，如tcpNoDelay和keepAlive
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            // 绑定端口号并接收传入的连接
            ChannelFuture f = bootstrap.bind(port).sync();
            // 关闭服务器
            f.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * @MethodName main
     * @Description https://www.w3cschool.cn/netty4userguide/
     * @Author xiaodong.zou
     * @Date 2019/10/15 23:38
     * @Param [args]
     * @return void
     **/
    public static void main(String[] args) throws Exception{
        int port = 8080;
        if(args.length > 0){
            port = Integer.parseInt(args[0]);
        }
        new DiscardServer(port).run();
    }
}
