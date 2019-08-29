package com.wisd.actuatordemo

import com.wisd.actuatordemo.bean.Response
import com.wisd.actuatordemo.bean.SysUser
import com.wisd.actuatordemo.security.MyUserDetailsService
import com.wisd.actuatordemo.security.UserRepository
import com.wisd.actuatordemo.utils.JwtUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created with IntelliJ IDEA.
 * @author scarlet* @time 2019/8/28 7:13
 */
@RestController('testc')
class TestController {
    @GetMapping('/test/auth')
    @PreAuthorize("hasAuthority('admin')")
    String test() {
        'auth'
    }

    @GetMapping('/test/role')
    @PreAuthorize("hasRole('ROLE_admin')")
    String role() {
        'role'
    }
    @Autowired
    UserRepository repository
    @Autowired
    MyUserDetailsService service
    @Autowired
    AuthenticationManager manager
    @Autowired
    JwtUtils jwtUtils

    @RequestMapping('/login')
    Response login(String username, String password) {
        def sysuser = new SysUser(username: username, password: password)
        def auth = new UsernamePasswordAuthenticationToken(sysuser, password, null)
        def authentication = manager.authenticate(auth)
        SecurityContextHolder.context.authentication = authentication
        def token = jwtUtils.generateAccessToken(authentication.principal as SysUser)

        jwtUtils.putToken(username, token)
        new Response(code: 200, msg: 'OK', data: token)
    }

}
