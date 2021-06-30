package raven.iss.web.security.permissions;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('LISTENER') and @customAuthenticationManager.listenerConferenceIdMatches(" +
        "authentication, #cid)")
public @interface ListenerPermission {
}
