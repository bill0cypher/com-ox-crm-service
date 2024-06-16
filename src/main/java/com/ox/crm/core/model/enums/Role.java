package com.ox.crm.core.model.enums;

public enum Role {
  ROLE_GUEST("GUEST"),
  ROLE_USER("USER"),
  ROLE_ADMIN("ADMIN");

  private final String roleName;

  Role(String role) {
    this.roleName = role;
  }

  public static String getRolesHierarchy() {
    return """
        ROLE_ADMIN > ROLE_USER
        ROLE_USER > ROLE_GUEST
        """;
  }

  @Override
  public String toString() {
    return roleName;
  }
}
