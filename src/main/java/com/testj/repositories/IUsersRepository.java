package com.testj.repositories;

import java.util.List;

import com.testj.repositories.models.UserDomainModel;

public interface IUsersRepository
{

    public List<UserDomainModel> getUsers();
    
    public UserDomainModel getUserById(long id);
    
    public UserDomainModel createUser(UserDomainModel user);
    
    public boolean updateUser(UserDomainModel user);
    
}
