package raven.iss.web.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import raven.iss.data.model.security.Profile;
import raven.iss.data.repositories.security.ProfileRepo;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component("userDetailsService")
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

   private final PasswordEncoder passwordEncoder;
   private final ProfileRepo profileRepo;
   public static final String noPass = "NOPASS";

   @Override
   @Transactional
   public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
      log.trace("Authenticating user '{}'", login);

      Profile profile = getUserProfile(login);
      return profile.getNoPass() ?
              createSpringSecurityUserNoPass(profile) : createSpringSecurityUser(profile);
   }

   private Profile getUserProfile(final String login) {
      Profile profile = profileRepo.findByUsername(login).orElseThrow(() ->
              new UsernameNotFoundException("User name: " + login + " not found"));
      if(!profile.isEnabled()) throw new UserNotActivatedException("User " + login + " is not activated");
      return profile;
   }

   private User createSpringSecurityUser(Profile user) {
      List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
              .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
              .collect(Collectors.toList());
      return new org.springframework.security.core.userdetails.User(user.getUsername(),
              user.getPassword(),
              grantedAuthorities);
   }

   private User createSpringSecurityUserNoPass(Profile user) {
      user.setNoPass(false);
      user = profileRepo.save(user);

      List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
              .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
              .collect(Collectors.toList());
      return new org.springframework.security.core.userdetails.User(user.getUsername(),
              passwordEncoder.encode(noPass),
              grantedAuthorities);
   }

}
