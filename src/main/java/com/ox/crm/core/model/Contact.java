package com.ox.crm.core.model;

import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@RequiredArgsConstructor
@Entity(name = "contact")
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "email_constraint", columnNames = "email")
})
@AllArgsConstructor
public class Contact extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private String phone;
  @OneToOne(mappedBy = "contact")
  private Client clientId;
  @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY)
  @ToString.Exclude
  private Set<Task> tasks;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "contacts_roles",
      joinColumns = @JoinColumn(name = "contact_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
  )
  @ToString.Exclude
  private Set<Role> roles;
}
