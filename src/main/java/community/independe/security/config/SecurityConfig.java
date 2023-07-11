package community.independe.security.config;

import com.nimbusds.jose.jwk.OctetSequenceKey;
import community.independe.repository.MemberRepository;
import community.independe.security.exception.JwtAccessDeniedHandler;
import community.independe.security.exception.JwtAuthenticationEntryPoint;
import community.independe.security.filter.CorsFilter;
import community.independe.security.filter.JwtAuthenticationFilter;
import community.independe.security.filter.JwtAuthorizationMacFilter;
import community.independe.security.handler.OAuth2AuthenticationFailureHandler;
import community.independe.security.handler.OAuth2AuthenticationSuccessHandler;
import community.independe.security.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import community.independe.security.service.oauth2.CustomOAuth2UserService;
import community.independe.security.signature.MacSecuritySigner;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final MacSecuritySigner macSecuritySigner;
    private final OctetSequenceKey octetSequenceKey;
    private final MemberRepository memberRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final CustomOAuth2UserService customOAuth2UserService;

    private String[] whiteList = {"/",
            "/actuator/**",
            "/ws",
            "/ws/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/oauth2/**",
            "/api/login",
            "/api/members/new",
            "/api/members/username",
            "/api/members/nickname",
            "/api/posts/main",
            "/api/posts/**"};

    private String[] blackList = {
            "/api/posts/new",
            "/api/oauth/members",
            "/api/posts/region/new",
            "/api/posts/independent/new",
            "/api/members/region",
            "/api/posts/update",
            "/api/chat/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.formLogin().disable();
        http.httpBasic().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

//        http.headers().frameOptions().disable();

        // stomp 사용을 위한 cors 적용
        http.cors().configurationSource(corsConfigurationSource());

//        http.exceptionHandling()
//                .authenticationEntryPoint(jwtAuthenticationEntryPoint())
//                        .accessDeniedHandler(jwtAccessDeniedHandler());

        http.authorizeHttpRequests()
                .requestMatchers(blackList).authenticated()
                .requestMatchers(whiteList).permitAll()
                .anyRequest().authenticated();

//        http.authorizeHttpRequests()
//                        .anyRequest().permitAll();

        http.oauth2Login()
                .redirectionEndpoint()
                    .baseUri("/oauth2/login/oauth2/code/*")
                        .and()
                            .userInfoEndpoint()
                                .userService(customOAuth2UserService)
                                    .and()
                                        .authorizationEndpoint()
                                            .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                                                .and()
                                                    .successHandler(oAuth2AuthenticationSuccessHandler());
    //                                                                                .failureHandler(oAuth2AuthenticationFailureHandler())

        http.addFilterBefore(jwtAuthorizationMacFilter(octetSequenceKey), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(macSecuritySigner, octetSequenceKey), UsernamePasswordAuthenticationFilter.class);

        http.userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public JwtAccessDeniedHandler jwtAccessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }

    @Bean
    public JwtAuthorizationMacFilter jwtAuthorizationMacFilter(OctetSequenceKey octetSequenceKey) {
        return new JwtAuthorizationMacFilter(octetSequenceKey, memberRepository);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // Authentication Manger
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(MacSecuritySigner macSecuritySigner, OctetSequenceKey octetSequenceKey) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(macSecuritySigner, octetSequenceKey);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager(null));
        return jwtAuthenticationFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        // frontend domain
        config.addAllowedOrigin("http://localhost:8081");
        config.addAllowedOrigin("https://www.independe.co.kr");
        config.addAllowedOrigin("https://independe.co.kr");
        config.addAllowedOrigin("https://api.independe.co.kr");
//        config.addAllowedOrigin("https://api.independe.co.kr");
//        config.addAllowedOrigin("http://localhost:8081");
//        config.addAllowedOrigin("*");
        // credential true 해야 채팅 가능
        config.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter();
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
//        return new OAuth2AuthenticationSuccessHandler(macSecuritySigner, octetSequenceKey);
        return new OAuth2AuthenticationSuccessHandler(macSecuritySigner, octetSequenceKey, httpCookieOAuth2AuthorizationRequestRepository);
    }

    @Bean
    OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler();
    }
}
