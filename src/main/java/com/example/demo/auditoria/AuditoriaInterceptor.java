package com.example.demo.auditoria;

import com.example.demo.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuditoriaInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaInterceptor.class);

    private final UsuarioRepository usuarioRepository;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        AuditoriaContext.inicializar();

        // 1. Obtener ID del usuario autenticado desde SecurityContext + BD
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Long idUsuario = usuarioRepository.findByEmail(auth.getName())
                    .map(u -> u.getId())
                    .orElse(0L);
            AuditoriaContext.setIdUsuario(idUsuario);
        } else {
            AuditoriaContext.setIdUsuario(0L);
        }

        // 2. IP pura válida para tipo INET en PostgreSQL
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0].trim();
        }
        AuditoriaContext.setIpOrigen(ip);

        // 3. HTTP, correlación y versión
        AuditoriaContext.setMetodoHttp(request.getMethod());
        AuditoriaContext.setEndpoint(request.getRequestURI());
        AuditoriaContext.setIdInteraccion(UUID.randomUUID());
        AuditoriaContext.setVersionAplicacion(appVersion);

        log.debug("[Auditoria] Context inicializado -> usuario={} ip={} endpoint={}",
                AuditoriaContext.getIdUsuario(), ip, request.getRequestURI());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuditoriaContext.limpiar();
    }
}