package com.wisd.actuatordemo.security

import com.google.gson.Gson
import com.wisd.actuatordemo.bean.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * @author scarlet* @time 2019/8/28 14:45
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfigure extends WebSecurityConfigurerAdapter {
    @Autowired
    MySecurityFilter filter

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, '/**')
                .permitAll()
                .antMatchers('/login').permitAll()
                .anyRequest()
                .authenticated()

        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

//        http
//                .formLogin()
//                .permitAll()
//                .successHandler({ HttpServletRequest request, HttpServletResponse response, Authentication authentication ->
//                    response.contentType = 'application/json;charset=utf-8'
//                    response.writer.write(new Gson().toJson(new Response(code: 200, msg: '登陆成功')))
//                })
//                .failureHandler({ HttpServletRequest request, HttpServletResponse response, AuthenticationException exception ->
//                    response.contentType = 'application/json;charset=utf-8'
//                    response.writer.write(new Gson().toJson(new Response(code: 500, msg: '登陆失败')))
//                })

        http
                .exceptionHandling()
                .authenticationEntryPoint({ HttpServletRequest request, HttpServletResponse response, AuthenticationException authException ->
                    response.contentType = 'application/json;charset=utf-8'
                    response.writer.write(new Gson().toJson(new Response(code: 700, msg: '尚未登陆')))
                })
                .accessDeniedHandler { HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException ->
                    response.contentType = 'application/json;charset=utf-8'
                    response.writer.write(new Gson().toJson(new Response(code: 600, msg: '权限不足')))
                }

//        http
//                .logout()
//                .permitAll()
//                .logoutSuccessHandler { HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication ->
//                    response.contentType = 'application/json;charset=utf-8'
//                    response.writer.write(new Gson().toJson(new Response(code: 200, msg: '注销成功')))
//                }

        http
                .cors()
                .and()
                .csrf()
                .disable()

        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter)
    }

    @Autowired
    MyUserDetailsService userDetailsService

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        def provider = new AuthenticationProvider() {
//            @Override
//            Authentication authenticate(Authentication authentication) throws AuthenticationException {
//                def username = authentication.getName()
//                def password = authentication.getCredentials()
//                def sysUser = userDetailsService.loadUserByUsername(username)
//                if (!(password + '6').equals(sysUser.password)) {
//                    throw new DisabledException('登陆验证失败')
//                }
//                new UsernamePasswordAuthenticationToken(sysUser, sysUser.password, sysUser.authorities)
//            }
//
//            @Override
//            boolean supports(Class<?> authentication) {
//                return true
//            }
//        }
//        auth.userDetailsService(userDetailsService)
//        auth.authenticationProvider(provider)
//    }

    @Bean
    AuthenticationManager manager() {
        new AuthenticationManager() {
            @Override
            Authentication authenticate(Authentication authentication) {
                def username = authentication.getName()
                def password = authentication.getCredentials()
                def sysUser = userDetailsService.loadUserByUsername(username)
                if (!(password + '6').equals(sysUser.password)) {
                    throw new DisabledException('登陆验证失败')
                }
                new UsernamePasswordAuthenticationToken(sysUser, sysUser.password, sysUser.authorities)
            }
        }
    }


}
