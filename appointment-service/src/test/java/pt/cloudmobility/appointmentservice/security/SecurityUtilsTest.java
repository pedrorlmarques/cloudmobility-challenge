package pt.cloudmobility.appointmentservice.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.util.context.Context;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityUtilsTest {

    @Test
    void testGetCurrentUserLogin() {
        var login = SecurityUtils
                .getCurrentUserLogin()
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin")))
                .block();
        assertThat(login).isEqualTo("admin");
    }

    @Test
    void testIsAuthenticated() {
        var isAuthenticated = SecurityUtils
                .isAuthenticated()
                .contextWrite(ReactiveSecurityContextHolder
                        .withAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin")))
                .block();
        assertThat(isAuthenticated).isTrue();
    }

    @Test
    void testAnonymousIsNotAuthenticated() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
        var isAuthenticated = SecurityUtils
                .isAuthenticated()
                .contextWrite(ReactiveSecurityContextHolder
                        .withAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin", authorities)))
                .block();
        assertThat(isAuthenticated).isFalse();
    }

    @Test
    void testHasCurrentUserThisAuthority() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.DOCTOR));
        var context = ReactiveSecurityContextHolder.withAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "admin", authorities)
        );
        var hasCurrentUserThisAuthority = SecurityUtils
                .hasCurrentUserThisAuthority(AuthoritiesConstants.DOCTOR)
                .contextWrite(context)
                .block();
        assertThat(hasCurrentUserThisAuthority).isTrue();

        hasCurrentUserThisAuthority = SecurityUtils
                .hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)
                .contextWrite(context).block();
        assertThat(hasCurrentUserThisAuthority).isFalse();
    }
}
