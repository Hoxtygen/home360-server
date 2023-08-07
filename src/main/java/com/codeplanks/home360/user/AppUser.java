package com.codeplanks.home360.user;

import com.codeplanks.home360.listing.Listing;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"}, name = "email"),
        @UniqueConstraint(columnNames = {"phoneNumber"}, name = "phoneNumber")
})
public class AppUser implements UserDetails {
  @Id
  @SequenceGenerator(name = "users_id_sequence", sequenceName = "users_id_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_sequence")
  private Integer id;

  @Column(name="firstName", length=50, nullable=false)
  private String firstName;

  @Column(name="lastName", length=50, nullable=false)
  private String lastName;

  @Column(name="email", nullable=false, unique=true)
  private String email;

  @Column(name="password", nullable=false)
  private String password;

  @Column(name="address", nullable=false)
  private String address;

  @Column(name="phoneNumber", length=11, nullable=false, unique=true)
  private String phoneNumber;

  @Column(name = "createdAt", nullable = false)
  private Date createdAt;

  @Column(name = "updatedAt", nullable = false)
  private Date updatedAt;

  @Column(name="role", length=50, nullable=false)
  @Enumerated(EnumType.STRING)
  private Role role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public AppUser(String firstName, String lastName, String email, String password, String address,
                 String phoneNumber, Date createdAt, Date updatedAt, Role role) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.address = address;
    this.phoneNumber = phoneNumber;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.role = role;
  }
}
