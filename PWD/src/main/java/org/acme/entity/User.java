package org.acme.entity;

import lombok.Getter;
import lombok.Setter;
import org.acme.exceptions.UserFormatException;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
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

    @Email
    String userName;

    @Column(length = 2040)
    @Length(min = 30,max = 2040)
    @NotNull
    String pwd;
    String phoneNumber;
}
