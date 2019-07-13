package ro.am.docgen.manager.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	private final DataSource dataSource;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication()
				.usersByUsernameQuery("SELECT username, password, enabled FROM application_user WHERE username = ?")
				.authoritiesByUsernameQuery("SELECT ? AS username, 'USER' AS role")
				.dataSource(dataSource)
				.passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/login", "/login.html", "/assets/*").permitAll()
				.anyRequest().authenticated().and()
				.csrf().disable()
				.formLogin()
				.loginPage("/login.html")
				.loginProcessingUrl("/login")
				.usernameParameter("username")
				.passwordParameter("password").and()
				.exceptionHandling().and()
				.logout()
				.logoutUrl("/logout").and();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(11);
	}

}
