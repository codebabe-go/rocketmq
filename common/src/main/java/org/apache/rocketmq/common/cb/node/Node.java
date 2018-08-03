package org.apache.rocketmq.common.cb.node;

/**
 * author: code.babe
 * date: 2018-07-31 14:15
 *
 * 机器阶段信息
 */
public class Node {

    public Node(String ip, Integer type) {
        this.ip = ip;
        this.type = type;
    }

    public interface Type {
        int MASTER = 1;
        int FOLLOWER = 2;
    }

    private String ip;
    private Integer type;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Node{" +
                "ip='" + ip + '\'' +
                ", type=" + type +
                '}';
    }
}
