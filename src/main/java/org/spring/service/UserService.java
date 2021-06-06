package org.spring.service;

import lombok.Getter;
import lombok.Setter;
import org.spring.annotation.Autowired;
import org.spring.annotation.Component;
import org.spring.annotation.Scope;

@Component
@Scope("prototype")
@Getter
@Setter
public class UserService {

    @Autowired
    private OrderService orderService;


}
