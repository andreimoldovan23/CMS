package raven.iss.web.controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import raven.iss.data.api.dtos.LoginDTO;
import raven.iss.data.api.dtos.UserDTO;
import raven.iss.data.model.security.Profile;
import raven.iss.data.services.interfaces.UserService;
import raven.iss.web.security.config.CustomAuthenticationManager;
import raven.iss.web.security.jwt.SecurityUtils;
import raven.iss.web.security.jwt.UserDetailsService;
import raven.iss.web.security.jwt.config.JWTFilter;
import raven.iss.web.security.jwt.config.JWTTokenProvider;
import raven.iss.web.security.permissions.AdminPermission;
import raven.iss.web.security.permissions.UserPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static raven.iss.web.controllers.UtilsHelper.getUsername;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final JWTTokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserService userService;
    private final CustomAuthenticationManager authenticationManager;

    private String getToken(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenProvider.createToken(authentication);
    }

    private ResponseEntity<JWTToken> responseToken(String jwt) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/api/authenticate")
    public ResponseEntity<JWTToken> authorize(@Validated @RequestBody LoginDTO loginDto) {

        if(userService.findByUsername(loginDto.getUsername()) == null)
            throw new UsernameNotFoundException("No user: " + loginDto.getUsername());

        String jwt = getToken(loginDto.getUsername(), loginDto.getPassword());
        return responseToken(jwt);
    }

    @UserPermission
    @GetMapping("/api/user")
    public UserDTO getCurrentUser() {
        String username = getUsername();
        return userService.findDTObyUsername(username);
    }

    @AdminPermission
    @GetMapping("/api/user/all")
    public List<UserDTO> getAllUsers() {
        return userService.getAll();
    }

    @UserPermission
    @GetMapping("/api/user/type")
    public Role getUserType() {
        return new Role(SecurityUtils.getCurrentAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.equals("USER") || authority.equals("ADMIN"))
                .collect(Collectors.toList()));
    }

    @UserPermission
    @GetMapping("/api/conferences/{cid}/user/type")
    public Role getUserConferenceRoles(@PathVariable Integer cid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> conferenceRoles = new ArrayList<>();
        if (authenticationManager.authorConferenceIdMatches(authentication, cid))
            conferenceRoles.add("AUTHOR");
        if (authenticationManager.chairConferenceIdMatches(authentication, cid))
            conferenceRoles.add("CHAIR");
        if (authenticationManager.listenerConferenceIdMatches(authentication, cid))
            conferenceRoles.add("LISTENER");
        if (authenticationManager.pcMemberConferenceIdMatches(authentication, cid))
            conferenceRoles.add("PCMEMBER");
        return new Role(conferenceRoles);
    }

    @UserPermission
    @PostMapping("/api/conferences/{cid}/registerAsAuthor")
    public ResponseEntity<JWTToken> registerAuthor(@PathVariable Integer cid) {
        String username = getUsername();
        userService.registerAuthor(username, cid);
        return responseToken(getToken(username, UserDetailsService.noPass));
    }

    @UserPermission
    @PostMapping("/api/conferences/{cid}/registerAsListener")
    public ResponseEntity<JWTToken> registerListener(@PathVariable Integer cid) {
        String username = getUsername();
        userService.registerListener(username, cid);
        return responseToken(getToken(username, UserDetailsService.noPass));
    }

    @PostMapping("/api/user/new")
    public void signup(@Validated @RequestBody LoginDTO userDTO) {
        userService.signUp(userDTO);
    }

    @Getter
    @AllArgsConstructor
    public static class Role {
        private final List<String> roles;
    }

    @Getter
    @RequiredArgsConstructor
    static class JWTToken {
        private final String idToken;
    }

}
