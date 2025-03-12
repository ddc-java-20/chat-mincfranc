package edu.cnm.deepdive.chat.model.dto;

import com.google.gson.annotations.Expose;
import java.util.UUID;

public class Channel {

  //getting these from the output in our scratch.

  @Expose(serialize = false)
  private UUID key;

  @Expose(serialize = false)
  private String name;

  @Expose(serialize = false)
  private boolean active;

  public UUID getKey() {
    return key;
  }

  public void setKey(UUID key) {
    this.key = key;
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
}
