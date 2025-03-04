package edu.cnm.deepdive.chat.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(
    indexes = {
        @Index(columnList = "active,name")
    }
)
public class Channel {

  @Id
  @GeneratedValue
  @Column(name = "channel_id", nullable = false, updatable = false)
  private long id;

  @Column(nullable = false, updatable = false, unique = true)
  private UUID externalKey;

  @Column(nullable = false, updatable = false, unique = true, length = 50)
  private String name;

  @Column(nullable = false, updatable = true)
  private boolean active;

  public long getId() {
    return id;
  }

  public UUID getExternalKey() {
    return externalKey;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public int hashCode() {
    return Long.hashCode(id);
  }

  @Override
  public boolean equals(Object obj) {
    boolean result;
    if (obj == this) {
      result = true;
    } else if (obj instanceof Channel other) {
      result = (id != 0 && id == other.id);
    } else {
      result = false;
    }
    return result;
  }

  @PrePersist
  void generateFieldValues() {
    externalKey = UUID.randomUUID();
  }


}
