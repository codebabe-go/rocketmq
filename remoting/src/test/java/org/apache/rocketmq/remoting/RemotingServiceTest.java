package org.apache.rocketmq.remoting;

import io.netty.channel.ChannelHandlerContext;
import org.apache.rocketmq.remoting.exception.*;
import org.apache.rocketmq.remoting.netty.*;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executors;

/**
 * author: code.babe
 * date: 2018-07-20 17:55
 */
public class RemotingServiceTest {

    private static final int SEND = 666;
    private static final int RECEIVE = 667;

    @Test
    public void testClient() throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException, UnsupportedEncodingException, RemotingTooMuchRequestException {
        NettyClientConfig clientConfig = new NettyClientConfig();
        RemotingClient client = new NettyRemotingClient(clientConfig); // 使用默认配置
//        client.registerProcessor(SEND, new NettyRequestProcessor() {
//            @Override
//            public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
//                System.out.printf("client process %s", new String(request.getBody(), "UTF-8"));
//                return request;
//            }
//
//            @Override
//            public boolean rejectRequest() {
//                return false;
//            }
//        }, Executors.newFixedThreadPool(10));
        client.start();
        RemotingCommand sendCmd = RemotingCommand.createRequestCommand(SEND, new CommandCustomHeader() {
            @Override
            public void checkFields() throws RemotingCommandException {
                // no-check
            }
        });
        client.invokeSync("localhost:8888", sendCmd, 10000L);
    }

    @Test
    public void testServer() throws InterruptedException, RemotingTimeoutException, RemotingSendRequestException {
        NettyServerConfig serverConfig = new NettyServerConfig();
        RemotingServer server = new NettyRemotingServer(serverConfig);
        server.registerProcessor(SEND, new NettyRequestProcessor() {
            @Override
            public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
                System.out.printf("server process %s", new String(request.getBody(), "UTF-8"));
                return request;
            }

            @Override
            public boolean rejectRequest() {
                return false;
            }
        }, Executors.newFixedThreadPool(10));
        server.start();
    }

}
