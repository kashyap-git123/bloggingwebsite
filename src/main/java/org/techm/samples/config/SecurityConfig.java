/*
 * package org.techm.samples.config;
 * 
 * import org.springframework.context.annotation.Bean; import
 * org.springframework.context.annotation.Configuration; import
 * org.springframework.security.config.Customizer; import
 * org.springframework.security.config.annotation.web.builders.HttpSecurity;
 * import org.springframework.security.web.SecurityFilterChain;
 * 
 * @Configuration public class SecurityConfig {
 * 
 * @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http)
 * throws Exception { http .csrf(csrf -> csrf.disable())
 * .authorizeHttpRequests(auth -> auth .requestMatchers("/blog/**").permitAll()
 * .anyRequest().authenticated() ) .httpBasic(Customizer.withDefaults());
 * 
 * return http.build(); } }
 */
/*package org.techm.samples.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
<<<<<<< HEAD
                .requestMatchers("/blog/users/login", "/blog/users/register", "/css/**", "/js/**").permitAll()
                .requestMatchers("/blog/users/blogger/**").hasRole("BLOGGER")
                .requestMatchers("/blog/users/reader/**").hasRole("READER")
                .anyRequest().authenticated()
=======
                .requestMatchers("/blog/**").permitAll()
                .requestMatchers("/api/reply/**").permitAll()
                .anyRequest().authenticated() 
>>>>>>> branch 'master' of https://sandeepreddy04-admin@bitbucket.org/sandeepreddy04/bloggingwebsite.git
            )
            .formLogin(form -> form
                .loginPage("/blog/users/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/blog/users/redirect", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/blog/users/login?logout")
                .permitAll()
            );

        return http.build();
    }
}*/
package org.techm.samples.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF protection disabled as per your setup
            .authorizeHttpRequests(auth -> auth
                // ✅ Public access for login, registration, static files
                .requestMatchers("/blog/users/login", "/blog/users/register", "/css/**", "/js/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/blog/users/register").permitAll()

                // ✅ Public access to view published posts by all users
                .requestMatchers(HttpMethod.GET, "/posts/all").permitAll()
                .requestMatchers(HttpMethod.GET, "/posts/view/**").permitAll()

                // ✅ Blogger dashboard & features
                .requestMatchers("/blog/users/blogger/**").hasRole("BLOGGER")
                .requestMatchers(HttpMethod.GET, "/posts/mine", "/posts/edit/**", "/posts/drafts", "/posts/create").hasRole("BLOGGER")
                .requestMatchers(HttpMethod.POST, "/posts/create", "/posts/edit/**", "/posts/delete/**", "/posts/publish/**").hasRole("BLOGGER")
                .requestMatchers(HttpMethod.DELETE, "/posts/**").hasRole("BLOGGER")

                // ✅ Guest dashboard access
                .requestMatchers("/blog/users/guest/**").hasRole("GUEST")

                // 🚦 Catch all
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/blog/users/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/blog/users/redirect", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/blog/users/login?logout")
                .permitAll()
            );

        return http.build();
    }

    // ✅ Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
