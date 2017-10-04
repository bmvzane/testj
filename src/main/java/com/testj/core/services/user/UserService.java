package com.testj.core.services.user;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import com.google.inject.Inject;
import com.testj.core.services.user.models.UserDTO;
import com.testj.repositories.IUsersRepository;
import com.testj.repositories.models.UserDomainModel;

public class UserService
{

    private final ModelMapper mapper;
    private final IUsersRepository usersRepository;

    @Inject
    public UserService(
        ModelMapper mapper,
        IUsersRepository usersRepository
    ) {
        this.mapper          = mapper;
        this.usersRepository = usersRepository;
    }
    
    public List<UserDTO> getUsers()
    {
        List<UserDomainModel> users = this.usersRepository.getUsers();
        
        return this.mapper.map(users, new TypeToken<List<UserDTO>>() {}.getType());
    }
    
    public UserDTO getUserById(long id)
    {
        UserDomainModel userDomainModel = this.usersRepository.getUserById(id);
        
        return this.mapper.map(userDomainModel, UserDTO.class);
    }
    
    public UserDTO createUser(UserDTO userDTO)
    {
        UserDomainModel userDomainModel = this.mapper.map(userDTO, UserDomainModel.class);
        
        UserDomainModel newUserDomainModel = this.usersRepository.createUser(userDomainModel);
        
        return this.mapper.map(newUserDomainModel, UserDTO.class);
    }
    
    public boolean updateUser(UserDTO userDTO)
    {
        UserDomainModel userDomainModel = this.mapper.map(userDTO, UserDomainModel.class);
        
        return this.usersRepository.updateUser(userDomainModel);
    }
    
}
