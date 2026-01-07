package com.sky.WebSocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket服务
 */
@Component
@Slf4j
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {

    //存放会话对象
    private static Map<String, Session> sessionMap = new HashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        log.info("客户端：{} 建立连接",sid);
        sessionMap.put(sid, session);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        log.info("收到来自客户端： {} 的信息{}",sid, message);
    }

    /**
     * 连接关闭调用的方法
     *
     * @param sid
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        log.info("连接断开：{}",sid);
        sessionMap.remove(sid);
    }

    /**
     * 群发
     *
     * @param message
     */
    public void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            try {
                //服务器向客户端发送消息
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 向指定客户端发送消息
     *
     * @param sid 客户端标识
     * @param message 消息内容
     */
    public void sendToClient(String sid, String message) {
        Session session = sessionMap.get(sid);
        if (session != null) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                log.error("向客户端{}发送消息失败: {}", sid, e.getMessage());
            }
        }
    }

    /**
     * 向管理端推送消息（新订单提醒、催单等）
     *
     * @param type 消息类型：1-新订单提醒，2-客户催单
     * @param message 消息内容（JSON格式）
     */
    public void sendToAdminClient(Integer type, String message) {
        // 向所有管理端客户端推送（sid以admin开头）
        for (Map.Entry<String, Session> entry : sessionMap.entrySet()) {
            String sid = entry.getKey();
            if (sid != null && sid.startsWith("admin")) {
                try {
                    entry.getValue().getBasicRemote().sendText(message);
                    log.info("向管理端客户端{}推送消息，类型：{}", sid, type);
                } catch (Exception e) {
                    log.error("向管理端客户端{}推送消息失败: {}", sid, e.getMessage());
                }
            }
        }
    }

}
