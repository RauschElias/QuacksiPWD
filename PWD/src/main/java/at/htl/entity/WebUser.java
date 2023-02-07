package at.htl.entity;

import lombok.Getter;
import lombok.Setter;
import org.checkerframework.common.aliasing.qual.Unique;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter

@NamedQueries({
    @NamedQuery(name = "getUserByName" ,query="select u from WebUser u where u.userName = ?1"),
    @NamedQuery(name = "updatePwd" ,query="update WebUser u set u.pwd = ?1 where u.userName = ?2")
})

@Entity
public class WebUser {

    @Id
    @GeneratedValue
    int id;

    @NotNull
    @Unique
    String userName;

    @Column(length = 2040)
    @Length(min = 30,max = 2040)
    @NotNull
    String pwd;

    @Unique
    @NotNull
    @Email
    String email;

    @NotNull
    String pwSalt;
}
