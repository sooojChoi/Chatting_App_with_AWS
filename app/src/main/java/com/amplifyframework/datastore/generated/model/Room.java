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
  public static final QueryField LAST_MSG = field("Room", "last_msg");
  public static final QueryField LAST_MSG_SENDER = field("Room", "last_msg_sender");
  public static final QueryField MEMBERS = field("Room", "members");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="String", isRequired = true) String last_msg_time;
  private final @ModelField(targetType="String") String last_msg;
  private final @ModelField(targetType="String") String last_msg_sender;
  private final @ModelField(targetType="String", isRequired = true) String members;
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
  
  public String getLastMsg() {
      return last_msg;
  }
  
  public String getLastMsgSender() {
      return last_msg_sender;
  }
  
  public String getMembers() {
      return members;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Room(String id, String name, String last_msg_time, String last_msg, String last_msg_sender, String members) {
    this.id = id;
    this.name = name;
    this.last_msg_time = last_msg_time;
    this.last_msg = last_msg;
    this.last_msg_sender = last_msg_sender;
    this.members = members;
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
              ObjectsCompat.equals(getLastMsg(), room.getLastMsg()) &&
              ObjectsCompat.equals(getLastMsgSender(), room.getLastMsgSender()) &&
              ObjectsCompat.equals(getMembers(), room.getMembers()) &&
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
      .append(getLastMsg())
      .append(getLastMsgSender())
      .append(getMembers())
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
      .append("last_msg=" + String.valueOf(getLastMsg()) + ", ")
      .append("last_msg_sender=" + String.valueOf(getLastMsgSender()) + ", ")
      .append("members=" + String.valueOf(getMembers()) + ", ")
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
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      last_msg_time,
      last_msg,
      last_msg_sender,
      members);
  }
  public interface NameStep {
    LastMsgTimeStep name(String name);
  }
  

  public interface LastMsgTimeStep {
    MembersStep lastMsgTime(String lastMsgTime);
  }
  

  public interface MembersStep {
    BuildStep members(String members);
  }
  

  public interface BuildStep {
    Room build();
    BuildStep id(String id);
    BuildStep lastMsg(String lastMsg);
    BuildStep lastMsgSender(String lastMsgSender);
  }
  

  public static class Builder implements NameStep, LastMsgTimeStep, MembersStep, BuildStep {
    private String id;
    private String name;
    private String last_msg_time;
    private String members;
    private String last_msg;
    private String last_msg_sender;
    public Builder() {
      
    }
    
    private Builder(String id, String name, String last_msg_time, String last_msg, String last_msg_sender, String members) {
      this.id = id;
      this.name = name;
      this.last_msg_time = last_msg_time;
      this.last_msg = last_msg;
      this.last_msg_sender = last_msg_sender;
      this.members = members;
    }
    
    @Override
     public Room build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Room(
          id,
          name,
          last_msg_time,
          last_msg,
          last_msg_sender,
          members);
    }
    
    @Override
     public LastMsgTimeStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public MembersStep lastMsgTime(String lastMsgTime) {
        Objects.requireNonNull(lastMsgTime);
        this.last_msg_time = lastMsgTime;
        return this;
    }
    
    @Override
     public BuildStep members(String members) {
        Objects.requireNonNull(members);
        this.members = members;
        return this;
    }
    
    @Override
     public BuildStep lastMsg(String lastMsg) {
        this.last_msg = lastMsg;
        return this;
    }
    
    @Override
     public BuildStep lastMsgSender(String lastMsgSender) {
        this.last_msg_sender = lastMsgSender;
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
    private CopyOfBuilder(String id, String name, String lastMsgTime, String lastMsg, String lastMsgSender, String members) {
      super(id, name, last_msg_time, last_msg, last_msg_sender, members);
      Objects.requireNonNull(name);
      Objects.requireNonNull(last_msg_time);
      Objects.requireNonNull(members);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder lastMsgTime(String lastMsgTime) {
      return (CopyOfBuilder) super.lastMsgTime(lastMsgTime);
    }
    
    @Override
     public CopyOfBuilder members(String members) {
      return (CopyOfBuilder) super.members(members);
    }
    
    @Override
     public CopyOfBuilder lastMsg(String lastMsg) {
      return (CopyOfBuilder) super.lastMsg(lastMsg);
    }
    
    @Override
     public CopyOfBuilder lastMsgSender(String lastMsgSender) {
      return (CopyOfBuilder) super.lastMsgSender(lastMsgSender);
    }
  }
  

  public static class RoomIdentifier extends ModelIdentifier<Room> {
    private static final long serialVersionUID = 1L;
    public RoomIdentifier(String id) {
      super(id);
    }
  }
  
}
