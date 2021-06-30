package raven.iss.web.security.permissions;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('CHAIR') and @customAuthenticationManager.chairConferenceIdMatches(authentication," +
        "#cid)")
public @interface ChairPermission {
}
