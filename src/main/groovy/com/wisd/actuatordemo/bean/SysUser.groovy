package com.wisd.actuatordemo.bean

import org.hibernate.annotations.GenericGenerator
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

import javax.persistence.*

/**
 * Created with IntelliJ IDEA.
 * @author scarlet* @time 2019/8/28 11:18
 */
@Entity
@Table(name = 'sys_user')
class SysUser implements UserDetails {
    @Id
    @GeneratedValue(generator = 'uuid')
    @GenericGenerator(name = 'uuid', strategy = 'uuid')
    String id
    String username
    String password
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    Set<Role> roles = new HashSet<>()
    @Transient
    Collection<? extends GrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>()

    void confAuthorities() {
        authorities = roles.collect({
            [new SimpleGrantedAuthority(it.name), new SimpleGrantedAuthority("ROLE_$it.name")]
        }).flatten() as Collection<? extends GrantedAuthority>
    }


    @Override
    boolean isAccountNonExpired() {
        return true
    }

    @Override
    boolean isAccountNonLocked() {
        return true
    }

    @Override
    boolean isCredentialsNonExpired() {
        return true
    }

    @Override
    boolean isEnabled() {
        return true
    }
}
