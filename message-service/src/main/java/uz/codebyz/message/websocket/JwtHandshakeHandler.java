package uz.codebyz.message.websocket;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class JwtHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(org.springframework.http.server.ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        Object principal = attributes.get("principal");
        if (principal instanceof Principal p) {
            return p;
        }
        return super.determineUser(request, wsHandler, attributes);
    }
}
