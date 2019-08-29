package com.wisd.actuatordemo.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Created with IntelliJ IDEA.
 * @author scarlet* @time 2019/8/28 14:31
 */
@Service
class MyUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository repository

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        def sysuser = repository.findByUsername(username)
        sysuser.confAuthorities()
        sysuser
    }
}
