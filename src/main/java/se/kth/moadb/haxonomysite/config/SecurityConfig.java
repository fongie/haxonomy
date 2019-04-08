package se.kth.moadb.haxonomysite.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import se.kth.moadb.haxonomysite.application.CustomAuthenticationFailureHandler;
import se.kth.moadb.haxonomysite.application.CustomAuthenticationSuccessHandler;
import se.kth.moadb.haxonomysite.application.SecurityService;

import java.util.Arrays;
import java.util.Collections;

/**
 * The config class contain annotations @Configuration and @EnableTransactionManagement
 * Spring will create all the transaction management beans and make them available to the IOC container
 */
@EnableTransactionManagement
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final BCryptPasswordEncoder passWordEncoder;

    private final SecurityService securityService;

    @Autowired
    public SecurityConfig(SecurityService securityService, BCryptPasswordEncoder passWordEncoder) {
        this.securityService = securityService;
        this.passWordEncoder = passWordEncoder;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .exceptionHandling()
                .and()
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/terms").permitAll()
                .antMatchers(HttpMethod.GET, "/reports").permitAll()
                .antMatchers(HttpMethod.POST, "/registration").permitAll()
                .antMatchers(HttpMethod.POST, "/perform_login").permitAll()
                .antMatchers(HttpMethod.GET, "/login/success").permitAll()
                .antMatchers(HttpMethod.POST, "/perform_logout").permitAll()
                .antMatchers(HttpMethod.POST, "/terms").hasAuthority("taxonomist")
                .antMatchers(HttpMethod.POST, "/reports").hasAuthority("taxonomist")
                .and()
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/perform_login")
                .successHandler(new CustomAuthenticationSuccessHandler())
                .failureHandler(customAuthenticationFailureHandler())
                .and()
                .logout()
                .logoutUrl("/perform_logout");
    }

    /**
     * Spring authentication provider that uses BCryptPasswordEncoder and checks with our securityService
     * that checks with our database
     * @return
     */
    @Bean
    public DaoAuthenticationProvider authProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passWordEncoder); // uses BCryptPasswordEncoder
        provider.setUserDetailsService(securityService);
        return provider;
    }

    /**
     * Choose our our authProvider as authentication provider
     */
    protected void configure(AuthenticationManagerBuilder auth){
        auth.authenticationProvider(authProvider());
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }


    /**
     * CORS setup
     * @return
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}