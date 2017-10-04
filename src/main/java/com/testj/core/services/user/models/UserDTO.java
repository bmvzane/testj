package com.testj.core.services.user.models;

import org.hibernate.validator.constraints.Email;
import org.joda.time.DateTime;

public class UserDTO
{

    public Long id;
    
    @Email
    public String email;
    
    public String name;
    
    public DateTime created_at;
    
}
