package com.wisd.actuatordemo.security


import com.wisd.actuatordemo.utils.JwtUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * @author scarlet* @time 2019/8/29 7:05
 */
@Component
class MySecurityFilter extends OncePerRequestFilter {
    @Autowired
    JwtUtils jwtUtils

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        def token = request.getHeader('Authorization')

        def tokenStart = 'Bearer '
        if (!token || !token.startsWith(tokenStart)) {
            token = null
        } else {
            token = token.substring(tokenStart.length())
        }
        def username = jwtUtils.getUsernameFromToken(token)
        if (username && jwtUtils.containToken(username, token)) {

            if (!SecurityContextHolder.context.authentication) {
                def sysUser = jwtUtils.getUserFromToken(token)
                if (jwtUtils.validateToken(token, sysUser)) {
                    def auth = new UsernamePasswordAuthenticationToken(username, null, sysUser.authorities)
                    auth.details = new WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.context.authentication = auth
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}
