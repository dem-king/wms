package com.wms.auth.security;

import com.wms.auth.service.AuthorizeService;
import com.wms.auth.service.TokenService;
import com.wms.system.service.SysRoleService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final AuthorizeService authorizeService;
    private final SysRoleService sysRoleService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (token != null) {
            try {
                if (tokenService.validateToken(token)) {
                    Claims claims = tokenService.parseToken(token);
                    Long userId = Long.valueOf(claims.getSubject());
                    String username = claims.get("username", String.class);
                    List<String> roles = defaultList(sysRoleService.getRoleCodesByUserId(userId));
                    List<String> permissions = defaultList(authorizeService.getUserPermissions(userId));
                    List<SimpleGrantedAuthority> authorities = buildAuthorities(roles, permissions).stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    authentication.setDetails(username);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    tokenService.renewTokenIfNeeded(token);
                }
            } catch (Exception e) {
                log.debug("JWT认证处理异常: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private List<String> buildAuthorities(List<String> roles, List<String> permissions) {
        LinkedHashSet<String> authorities = new LinkedHashSet<>();
        roles.forEach(role -> authorities.add("ROLE_" + role));
        authorities.addAll(permissions);
        return new ArrayList<>(authorities);
    }

    private List<String> defaultList(Collection<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return values.stream()
                .filter(StringUtils::hasText)
                .toList();
    }
}
