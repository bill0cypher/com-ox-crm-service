package com.ox.crm.core.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppConstants {
  public static final String ALPHA_NUMERIC_REGEX = "^[\\w ]*[^\\W_][\\w ]*$";
  public static final String NUMERIC_REGEX = "/[^0-9]/g";

  public static final String BEARER_PREFIX = "Bearer ";
  public static final String PRIVILEGES = "Privileges";

  @UtilityClass
  public class Messages {
    public static final String SPECIAL_CHARACTERS_FORBIDDEN_MSG = "{special.characters.forbidden}";
    public static final String INVALID_ENUM_MSG = "{invalid.enum.value}";
  }

  @UtilityClass
  public class Logging {
    public static final String CLIENT_ID = "client_id";
    public static final String CONTACT_ID = "contact_id";
    public static final String TASK_ID = "task_id";
  }

  @UtilityClass
  public class Cache {
    public static final String CLIENTS_CACHE = "clientsCache";
    public static final String CONTACTS_CACHE = "contactsCache";
    public static final String TASKS_CACHE = "tasksCache";
  }
}
