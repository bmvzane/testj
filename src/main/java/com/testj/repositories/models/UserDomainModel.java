package com.testj.repositories.models;

import org.hibernate.validator.constraints.Email;
import org.joda.time.DateTime;

public class UserDomainModel extends AbstractDomainModel
{
    
    public Long id;
    
    @Email
    public String email;
    
    public String name;
    
    public Boolean status;
    
    public DateTime created_at;
    
    public DateTime updated_at;
    
}
