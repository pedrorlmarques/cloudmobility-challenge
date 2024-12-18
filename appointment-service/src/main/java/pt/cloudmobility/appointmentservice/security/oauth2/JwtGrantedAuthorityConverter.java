package pt.cloudmobility.appointmentservice.security.oauth2;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import pt.cloudmobility.appointmentservice.security.SecurityUtils;

import java.util.Collection;

@Component
public class JwtGrantedAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        return SecurityUtils.extractAuthorityFromClaims(jwt.getClaims());
    }
}
