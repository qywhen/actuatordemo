package com.wisd.actuatordemo.bean

import lombok.Builder

import javax.persistence.Entity
import javax.persistence.Id

/**
 * Created with IntelliJ IDEA.
 * @author scarlet* @time 2019/8/28 11:27
 */
@Entity
@Builder
class Role {
    @Id
    String id
    String name
}
