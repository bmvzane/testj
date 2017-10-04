package com.testj.repositories.Database;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.modelmapper.TypeToken;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.IDBI;

import com.google.inject.Inject;
import com.testj.repositories.IUsersRepository;
import com.testj.repositories.models.UserDomainModel;

public class UsersRepository extends AbstractDatabaseRepository implements IUsersRepository
{
    
    @Inject
    public UsersRepository(IDBI dbi)
    {
        super(dbi);
    }
    
    private List<UserDomainModel> mapUserListFromSelect(List<Map<String, Object>> results)
    {
        return this.getMapper().map(results, new TypeToken<List<UserDomainModel>>() {}.getType());
    }
    
    public List<UserDomainModel> getUsers()
    {
        Handle h         = this.getDBI().open();
        
        List<Map<String, Object>> results = h.select("select * from user");
        List<UserDomainModel> users       = this.mapUserListFromSelect(results);
        
        h.close();
        
        return users;
    }
    
    public UserDomainModel getUserById(long id)
    {
        Handle h         = this.getDBI().open();
        
        List<Map<String, Object>> results = h.select("select * from user where id = ? limit 1", id);
        UserDomainModel user              = this.mapUserListFromSelect(results)
                                                .stream()
                                                .findFirst()
                                                .orElse(null);
        
        h.close();
        
        return user;
    }
    
    public UserDomainModel createUser(UserDomainModel user)
    {
        Map<String, Object> sqlParams = new HashMap<String, Object>();
        if (user.email != null) {
            sqlParams.put("email", user.email);
        }
        
        if (user.name != null) {
            sqlParams.put("name", user.name);
        }
        
        if (user.status == null) {
            user.status = true;
        }
        
        sqlParams.put("status", user.status);
        
        if (sqlParams.isEmpty()) {
            return null;
        }
        
        Handle h        = this.getDBI().open();
        
        long userId = h.inTransaction((conn, status) -> {
            int affectedRows = conn.insert("insert into user ("+ String.join(", ", sqlParams.keySet()) +") values (?, ?, ?)", sqlParams.values().toArray());
            
            if (affectedRows > 0) {
                List<Map<String, Object>> results = h.select("select last_insert_id()");
                
                return ((BigInteger) results.get(0).values().toArray()[0]).longValue();
            }
            
            return (long) 0;
        });
        
        UserDomainModel newUser = null;
        if (userId > 0) {
            newUser = this.getUserById(userId);
        }
        
        h.close();
        
        return newUser;
    }
    
    public boolean updateUser(UserDomainModel user)
    {
        List<String> updateFields = new ArrayList<String>();
        List<Object> params       = new ArrayList<Object>();
        
        if (user.email != null) {
            updateFields.add("email = ?");
            params.add(user.email);
        }
        
        if (user.name != null) {
            updateFields.add("name = ?");
            params.add(user.name);
        }
        
        if (user.status != null) {
            updateFields.add("status = ?");
            params.add(user.status);
        }
        
        if (user.id == null || updateFields.isEmpty()) {
            return false;
        }
        
        updateFields.add("updated_at = ?");
        params.add(DateTime.now());
        
        params.add(user.id);
        
        Handle h        = this.getDBI().open();
        
        boolean success = h.inTransaction((conn, status) -> {
            int affectedRows = conn.update("update user set " + String.join(", ", updateFields) + " where id = ? limit 1", params.toArray());
            
            return affectedRows > 0;
        });
        
        h.close();
        
        return success;
    }

}
