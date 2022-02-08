package org.snax.supersnax.controller;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;

/**
 * @author maoth
 * @date 2022/1/11 19:40
 * @description
 */
@ServerEndpoint(value = "/test")
@Component
public class TestWebSocketServer {

    @OnOpen
    public void onOpen() {
        System.out.println("onOpen!");
    }

    @OnClose
    public void onClose() {
        System.out.println("on close");
    }

    @OnMessage
    public void onMessage(String id) {
        System.out.println("On message");
    }

    @OnError
    public void onError() {
        System.out.println("on error");
    }
}
