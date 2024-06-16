package com.ox.crm.core.model;

import java.time.Duration;
import java.util.UUID;

import com.ox.crm.core.model.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@RequiredArgsConstructor
@Entity(name = "task")
@AllArgsConstructor
public class Task extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private String description;
  @Enumerated(EnumType.STRING)
  private Status status;
  @ManyToOne
  @JoinColumn(name="contact_id", referencedColumnName = "id")
  private Contact contact;
  private Duration duration;
}
