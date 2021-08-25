package com.hocztms.security.config;


import com.hocztms.filters.IpLimitFilter;
import com.hocztms.jwt.JwtAuthTokenFilter;
import com.hocztms.security.hander.MyAccessDeniedHandler;
import com.hocztms.security.hander.MyAuthenticationEntryPoint;
import com.hocztms.security.hander.MyLogoutSuccessHandler;
import com.hocztms.security.servie.MyAdminDetailServiceImpl;
import com.hocztms.security.xss.XssFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;


@Configuration
@EnableWebSecurity
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyAdminDetailServiceImpl userDetailService;

    @Autowired
    private JwtAuthTokenFilter jwtAuthTokenFilter;

    @Autowired
    private XssFilter xssFilter;

    @Autowired
    private IpLimitFilter ipLimitFilter;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationProvider(wxLoginAuthenticationProvider());
        auth.userDetailsService(userDetailService).passwordEncoder(encoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

////        login表单提交
//        http.formLogin()
//                .loginPage("/login.html")
//                .loginProcessingUrl("/user/login");

//        基于jwt 所以取消session
        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/login.html","/user/login","/user/register","/user/logout","/user/getPassword").permitAll()

                .antMatchers("/index/**").permitAll()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/test/**").permitAll()
                .antMatchers("/user/**").hasAnyAuthority("admin","user")
                .antMatchers("/goods/**").hasAnyAuthority("admin","user")
                .antMatchers("/superAdmin/**").hasAnyAuthority("superAdmin")


                //静态资源过滤
                .antMatchers("/images/**").permitAll()
                .antMatchers("/client/**").permitAll()

//                socket测试
                .antMatchers("/socket/**").permitAll()

                //swagger过滤
                .antMatchers("/v2/api-docs", "/swagger-resources/configuration/ui",
                        "/swagger-resources", "/swagger-resources/configuration/security",
                        "/swagger-ui.html", "/webjars/**","swagger-ui.html/**").permitAll()
                .anyRequest().authenticated();



        //自定义未登录 无权限处理
        http.exceptionHandling()
                .accessDeniedHandler(new MyAccessDeniedHandler())
                .authenticationEntryPoint(new MyAuthenticationEntryPoint());

        http.logout().logoutSuccessHandler(new MyLogoutSuccessHandler());

        //jwt
        http.addFilterBefore(jwtAuthTokenFilter, UsernamePasswordAuthenticationFilter.class);
        //xss防护
        http.addFilterAfter(xssFilter, CsrfFilter.class);
        http.addFilterBefore(ipLimitFilter,CsrfFilter.class);
        http.csrf().disable();
        http.cors();
    }




    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public WxLoginAuthenticationProvider wxLoginAuthenticationProvider (){
        return new WxLoginAuthenticationProvider();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        return httpServletRequest -> {
            CorsConfiguration cfg = new CorsConfiguration();
            cfg.addAllowedHeader("*");
            cfg.addAllowedMethod("*");
            cfg.addAllowedOriginPattern("*");
            cfg.setAllowCredentials(true);
            return cfg;
        };
    }
}
