package me.codebabe.rocketmq.cbnamesrv;

import org.apache.rocketmq.remoting.netty.NettyClientConfig;
import org.apache.rocketmq.remoting.netty.NettyRequestProcessor;
import org.apache.rocketmq.remoting.netty.NettyServerConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * author: code.babe
 * date: 2018-07-25 20:46
 */
public class NamesrvStartup {

    public static void main(String[] args) {

        NettyClientConfig clientConfig = new NettyClientConfig();
        NettyServerConfig serverConfig = new NettyServerConfig();
        Map<Integer, NettyRequestProcessor> requestProcessorMap = new HashMap<>();
        String masterIp = "127.0.0.1";
        if (args != null && args.length == 1) { // 如果为空说明是主, 否则就是备
            masterIp = args[0];
        }

        NamesrvController controller = new NamesrvController(serverConfig, clientConfig, requestProcessorMap, masterIp);

        controller.init();

        controller.start();
    }

}
