package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.core.model.ModelIdentifier;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the Room type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Rooms", type = Model.Type.USER, version = 1)
public final class Room implements Model {
  public static final QueryField ID = field("Room", "id");
  public static final QueryField NAME = field("Room", "name");
  public static final QueryField LAST_MSG_TIME = field("Room", "last_msg_time");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="String", isRequired = true) String last_msg_time;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  /** @deprecated This API is internal to Amplify and should not be used. */
  @Deprecated
   public String resolveIdentifier() {
    return id;
  }
  
  public String getId() {
      return id;
  }
  
  public String getName() {
      return name;
  }
  
  public String getLastMsgTime() {
      return last_msg_time;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Room(String id, String name, String last_msg_time) {
    this.id = id;
    this.name = name;
    this.last_msg_time = last_msg_time;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Room room = (Room) obj;
      return ObjectsCompat.equals(getId(), room.getId()) &&
              ObjectsCompat.equals(getName(), room.getName()) &&
              ObjectsCompat.equals(getLastMsgTime(), room.getLastMsgTime()) &&
              ObjectsCompat.equals(getCreatedAt(), room.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), room.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getLastMsgTime())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Room {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("last_msg_time=" + String.valueOf(getLastMsgTime()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static NameStep builder() {
      return new Builder();
  }
  
  /**
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static Room justId(String id) {
    return new Room(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      last_msg_time);
  }
  public interface NameStep {
    LastMsgTimeStep name(String name);
  }
  

  public interface LastMsgTimeStep {
    BuildStep lastMsgTime(String lastMsgTime);
  }
  

  public interface BuildStep {
    Room build();
    BuildStep id(String id);
  }
  

  public static class Builder implements NameStep, LastMsgTimeStep, BuildStep {
    private String id;
    private String name;
    private String last_msg_time;
    public Builder() {
      
    }
    
    private Builder(String id, String name, String last_msg_time) {
      this.id = id;
      this.name = name;
      this.last_msg_time = last_msg_time;
    }
    
    @Override
     public Room build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Room(
          id,
          name,
          last_msg_time);
    }
    
    @Override
     public LastMsgTimeStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public BuildStep lastMsgTime(String lastMsgTime) {
        Objects.requireNonNull(lastMsgTime);
        this.last_msg_time = lastMsgTime;
        return this;
    }
    
    /**
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String name, String lastMsgTime) {
      super(id, name, last_msg_time);
      Objects.requireNonNull(name);
      Objects.requireNonNull(last_msg_time);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder lastMsgTime(String lastMsgTime) {
      return (CopyOfBuilder) super.lastMsgTime(lastMsgTime);
    }
  }
  

  public static class RoomIdentifier extends ModelIdentifier<Room> {
    private static final long serialVersionUID = 1L;
    public RoomIdentifier(String id) {
      super(id);
    }
  }
  
}
