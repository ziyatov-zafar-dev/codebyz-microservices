package uz.codebyz.message.websocket;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import uz.codebyz.message.security.JwtPrincipal;
import uz.codebyz.message.security.JwtTokenService;

import java.security.Principal;
import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenService jwtTokenService;

    public JwtHandshakeInterceptor(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Principal principal = buildPrincipal(token);
                attributes.put("principal", principal);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
        return true;
    }

    private Principal buildPrincipal(String token) {
        var authentication = jwtTokenService.parse(token);
        var jwtUser = (uz.codebyz.message.security.JwtUser) authentication.getPrincipal();
        return new JwtPrincipal(jwtUser.getUserId());
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // no-op
    }
}
