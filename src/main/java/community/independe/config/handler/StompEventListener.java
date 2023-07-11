package community.independe.config.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Slf4j
//@Component
public class StompEventListener {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String destination = accessor.getDestination();

        System.out.println("STOMP 연결 성공 - 세션 ID: " + sessionId);
        log.info("destination : {}", destination);

    }

    @EventListener
    public void handleStompSubscriptionEvent(AbstractSubProtocolEvent event) {
        if (event instanceof SessionSubscribeEvent) {
            SessionSubscribeEvent subscribeEvent = (SessionSubscribeEvent) event;
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(subscribeEvent.getMessage());
            String destination = accessor.getDestination();

            System.out.println("구독된 목적지: " + destination);
        }
    }

    @EventListener
    public void handleStompMessageEvent(AbstractSubProtocolEvent event) {
        if (event instanceof SessionUnsubscribeEvent) {
            SessionUnsubscribeEvent unsubscribeEvent = (SessionUnsubscribeEvent) event;
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(unsubscribeEvent.getMessage());
            String destination = accessor.getDestination();

            System.out.println("수신된 메시지 목적지: " + destination);
        }
    }
}
