package com.wisd.actuatordemo.security

import com.wisd.actuatordemo.bean.SysUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created with IntelliJ IDEA.
 * @author scarlet* @time 2019/8/28 14:31
 */
interface UserRepository extends JpaRepository<SysUser,String>{
    SysUser findByUsername(String username)
}
