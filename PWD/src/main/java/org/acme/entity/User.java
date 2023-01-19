package org.acme.entity;

import lombok.Getter;
import lombok.Setter;
import org.acme.exceptions.UserFormatException;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.regex.Pattern;

@Getter
@Setter

@NamedQueries({
    @NamedQuery(name = "getUserByName" ,query="select u from User u where u.userName = ?1"),
    @NamedQuery(name = "updatePwd" ,query="update User u set u.pwd = ?1 where u.userName = ?2")
})

@Entity
public class User {

    @Id
    int id;

    String userName;
    String pwd;
    String phoneNumber;

    public void check() throws UserFormatException {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);

        if(!pattern.matcher(userName).matches())
            throw new UserFormatException("Invalid E-Mail");
    }
}
