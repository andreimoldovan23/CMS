package raven.iss.web.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SecurityUtils {

   private SecurityUtils() {
   }

   public static Optional<String> getCurrentUsername() {
      final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if (authentication == null) {
         log.trace("no authentication in security context found");
         return Optional.empty();
      }

      String username = null;
      if (authentication.getPrincipal() instanceof UserDetails) {
         UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
         username = springSecurityUser.getUsername();
      } else if (authentication.getPrincipal() instanceof String) {
         username = (String) authentication.getPrincipal();
      }

      log.trace("found username '{}' in security context", username);

      return Optional.ofNullable(username);
   }

   public static List<GrantedAuthority> getCurrentAuthorities() {
      final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null) {
         log.trace("no authentication in security context found");
         return new ArrayList<>();
      }
      return new ArrayList<>(authentication.getAuthorities());
   }

}
