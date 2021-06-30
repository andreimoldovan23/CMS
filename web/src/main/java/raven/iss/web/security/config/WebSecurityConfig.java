package raven.iss.web.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import raven.iss.web.security.jwt.JwtAccessDeniedHandler;
import raven.iss.web.security.jwt.JwtAuthenticationEntryPoint;
import raven.iss.web.security.jwt.config.JWTConfig;
import raven.iss.web.security.jwt.config.JWTTokenProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JWTTokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint authenticationErrorHandler;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private JWTConfig securityConfigurerAdapter() {
        return new JWTConfig(tokenProvider);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers(
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/h2-console/**"
                );
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()

                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling()
                .authenticationEntryPoint(authenticationErrorHandler)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()
                .headers().frameOptions().disable()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.POST, "/api/authenticate").permitAll()
                .mvcMatchers(HttpMethod.POST, "/api/user/new").permitAll()
                .mvcMatchers(HttpMethod.GET, "/api/conferences").permitAll()
                .mvcMatchers(HttpMethod.GET, "/api/conferences/*").permitAll()
                .mvcMatchers(HttpMethod.GET, "/api/conferences/*/phases").permitAll()
                .mvcMatchers(HttpMethod.GET, "/api/conferences/*/phases/*").permitAll()
                .mvcMatchers(HttpMethod.POST, "/webhook").permitAll()
                .mvcMatchers(HttpMethod.GET, "/api/conferences/*/papers/*/presentationFile").permitAll()
                .mvcMatchers(HttpMethod.GET, "/api/conferences/*/papers/*/file").permitAll()

                .anyRequest().authenticated()

                .and()
                .apply(securityConfigurerAdapter());
    }

}
