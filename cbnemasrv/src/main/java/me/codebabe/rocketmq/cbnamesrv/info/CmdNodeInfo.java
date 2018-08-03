package me.codebabe.rocketmq.cbnamesrv.info;

import org.apache.rocketmq.common.cb.node.Node;
import org.apache.rocketmq.remoting.CommandCustomHeader;
import org.apache.rocketmq.remoting.exception.RemotingCommandException;

/**
 * author: code.babe
 * date: 2018-07-31 14:47
 */
public class CmdNodeInfo extends Node implements CommandCustomHeader {

    public CmdNodeInfo(String ip, Integer type) {
        super(ip, type);
    }

    @Override
    public void checkFields() throws RemotingCommandException {
        if (super.getIp() == null || super.getIp().equalsIgnoreCase("")) {
            throw new RemotingCommandException("node ip is not set");
        }
    }
}