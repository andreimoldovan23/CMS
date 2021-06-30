package raven.iss.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import raven.iss.web.security.jwt.SecurityUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecurityUtilsTest {

    private final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

    @BeforeEach
    public void setUp() {
        securityContext.setAuthentication(null);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void getCurrentUsername() {
        assertThat(SecurityUtils.getCurrentUsername()).isEmpty();

        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
        SecurityContextHolder.setContext(securityContext);

        Optional<String> username = SecurityUtils.getCurrentUsername();

        assertThat(username).contains("admin");
    }

    @Test
    public void getCurrentAuthorities() {
        assertThat(SecurityUtils.getCurrentAuthorities()).isEmpty();

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ADMIN");
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "",
                Collections.singleton(authority)));
        SecurityContextHolder.setContext(securityContext);

        List<GrantedAuthority> authorities = SecurityUtils.getCurrentAuthorities();
        assertEquals(authorities.size(), 1);
        assertEquals(authorities.get(0).getAuthority(), "ADMIN");
    }

}
