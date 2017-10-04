package com.testj.api.users;

import org.hibernate.validator.constraints.Email;
import org.joda.time.DateTime;

public class UserResourceModel
{

    public Long id;

    @Email
    public String email;

    public String name;

    public Boolean status;

    public DateTime createdAt;

}
