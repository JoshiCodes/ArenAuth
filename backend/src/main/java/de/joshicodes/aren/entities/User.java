package de.joshicodes.aren.entities;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@UserDefinition
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public UUID id;

    @Username
    @Column(unique = true, nullable = false)
    public String username;

    @Password
    @Column(nullable = false)
    public String password;

    @Column(unique = true)
    public String email;

    @Column(nullable = true, name = "avatar_id")
    public String avatarId;

    @Roles
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    public Set<String> roles = new HashSet<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    public List<Project> ownedProjects;

    /**
     * Factory Method to create new User
     */
    @Transient
    public static void add(String username, String clearTextPassword, String role, String email) {
        User user = new User();
        user.username = username;
        // hash password
        user.password = BcryptUtil.bcryptHash(clearTextPassword);
        user.email = email;
        user.roles.add(role);
        user.persist();
    }

}
