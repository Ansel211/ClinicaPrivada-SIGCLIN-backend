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

import java.net.Inet4Address;
import java.net.InetAddress;
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
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof com.example.demo.model.Usuario usuarioAuth) {
            AuditoriaContext.setIdUsuario(usuarioAuth.getId());
        } else if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String identificador = auth.getName();
            Long idUsuario = usuarioRepository.findByEmail(identificador)
                    .or(() -> usuarioRepository.findByUsername(identificador))
                    .map(u -> u.getId())
                    .orElse(0L);
            AuditoriaContext.setIdUsuario(idUsuario);
        } else {
            AuditoriaContext.setIdUsuario(0L);
        }
        
        // 2. IP de origen – solo se confía en la dirección TCP real de la conexión.
        String ip = normalizarIp(request.getRemoteAddr());

        if ("::1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (java.net.UnknownHostException e) {
                // Dejar la ip loopback tal cual si falla la resolución
            }
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

    private String normalizarIp(String ip) {
        if (ip == null || ip.isBlank()) return ip;

        int zonaIdx = ip.indexOf('%');
        if (zonaIdx != -1) {
            ip = ip.substring(0, zonaIdx);
        }

        String lower = ip.toLowerCase();
        if (lower.startsWith("::ffff:")) {
            return ip.substring(7);
        }

        try {
            InetAddress addr = InetAddress.getByName(ip);
            if (addr instanceof Inet4Address) {
                return addr.getHostAddress();
            }
            return addr.getHostAddress();
        } catch (java.net.UnknownHostException e) {
            return ip;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuditoriaContext.limpiar();
    }
}