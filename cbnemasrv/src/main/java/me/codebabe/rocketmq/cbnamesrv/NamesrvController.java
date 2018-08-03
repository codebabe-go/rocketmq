package me.codebabe.rocketmq.cbnamesrv;

import me.codebabe.rocketmq.cbnamesrv.info.CmdNodeInfo;
import org.apache.rocketmq.common.cb.MsgCmd;
import org.apache.rocketmq.common.cb.node.Node;
import org.apache.rocketmq.remoting.exception.RemotingConnectException;
import org.apache.rocketmq.remoting.exception.RemotingSendRequestException;
import org.apache.rocketmq.remoting.exception.RemotingTimeoutException;
import org.apache.rocketmq.remoting.netty.*;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * author: code.babe
 * date: 2018-07-25 20:47
 *
 * 监听获取服务信息, 服务器信息持久化
 */
public class NamesrvController {

    private NettyRemotingServer server; // 心跳服务器/消息处理服务器
    private NettyRemotingClient heartbeatClient; // 心跳客户端
    private Map<Integer, NettyRequestProcessor> processors;
    private ExecutorService processorPool;
    private String masterIp;
    private String localIp;
    private boolean isMaster;
    private List<Node> nodesInfo; // 各节点信息

    // 心跳cmd固定, 这里只需要给到处理器的processor即可
    public NamesrvController(NettyServerConfig serverConfig, NettyClientConfig clientConfig, Map<Integer, NettyRequestProcessor> processors, String masterIp, String localIp) {
        this.server = new NettyRemotingServer(serverConfig);
        this.heartbeatClient = new NettyRemotingClient(clientConfig);
        this.processors = processors;
        this.masterIp = masterIp;
        this.localIp = localIp;
        this.isMaster = masterIp.equalsIgnoreCase(localIp);
        this.processorPool = Executors.newFixedThreadPool(10, new ThreadFactory() {
            AtomicInteger count = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread();
                thread.setName("server-" + count.getAndIncrement());
                return thread;
            }
        });
    }

    public void init() {
        if (!processors.isEmpty()) {
            for (Map.Entry<Integer, NettyRequestProcessor> processorEntry : processors.entrySet()) {
                server.registerProcessor(processorEntry.getKey(), processorEntry.getValue(), processorPool);
            }
        }
    }

    public void start() {
        server.start();
        heartbeatClient.start();

        if (!isMaster) { // 保证心跳
            Executors.newScheduledThreadPool(10).scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    RemotingCommand heartbeatCmd = RemotingCommand.createRequestCommand(MsgCmd.HEARTBEAT, new CmdNodeInfo(localIp, CmdNodeInfo.Type.MASTER));
                    try {
                        heartbeatClient.invokeSync(masterIp, heartbeatCmd, 5000);
                    } catch (InterruptedException | RemotingConnectException | RemotingTimeoutException | RemotingSendRequestException e) { // FIXME: 不论什么情况一致认为是心跳挂了
                        e.printStackTrace();
                    }
                }
            }, 1000, 10, TimeUnit.SECONDS);
        }



    }

}
