package uz.codebyz.message.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import uz.codebyz.message.security.JwtPrincipal;
import uz.codebyz.message.security.JwtTokenService;
import uz.codebyz.message.security.JwtUser;

import java.security.Principal;

public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenService jwtTokenService;

    public StompAuthChannelInterceptor(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Authorization header is required for STOMP");
            }
            String token = authHeader.substring(7);
            var authentication = jwtTokenService.parse(token);
            JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
            Principal principal = new JwtPrincipal(jwtUser.getUserId());

            accessor.setUser(principal);
            if (accessor.getSessionAttributes() != null) {
                accessor.getSessionAttributes().put("principal", principal);
            }
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(jwtUser, null, authentication.getAuthorities())
            );
        } else {
            Principal user = accessor.getUser();
            if (user == null) {
                // SockJS disconnect/heartbeat bo'lsa user null bo'lishi mumkin, session attribute dan olamiz
                Object sessionPrincipal = accessor.getSessionAttributes() != null
                        ? accessor.getSessionAttributes().get("principal")
                        : null;
                if (sessionPrincipal instanceof Principal p) {
                    accessor.setUser(p);
                    user = p;
                }
            }
            StompCommand command = accessor.getCommand();
            if (user == null && (StompCommand.SEND.equals(command) || StompCommand.SUBSCRIBE.equals(command))) {
                throw new IllegalArgumentException("Unauthorized STOMP message");
            }
        }
        return ChannelInterceptor.super.preSend(message, channel);
    }
}
