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

/** This is an auto generated class representing the Group type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Groups", type = Model.Type.USER, version = 1)
public final class Group implements Model {
  public static final QueryField ID = field("Group", "id");
  public static final QueryField USER_ID = field("Group", "user_id");
  public static final QueryField ROOM_ID = field("Group", "room_id");
  public static final QueryField JOIN_TIME = field("Group", "join_time");
  public static final QueryField LEFT_TIME = field("Group", "left_time");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String user_id;
  private final @ModelField(targetType="String", isRequired = true) String room_id;
  private final @ModelField(targetType="String", isRequired = true) String join_time;
  private final @ModelField(targetType="String") String left_time;
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
  
  public String getUserId() {
      return user_id;
  }
  
  public String getRoomId() {
      return room_id;
  }
  
  public String getJoinTime() {
      return join_time;
  }
  
  public String getLeftTime() {
      return left_time;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Group(String id, String user_id, String room_id, String join_time, String left_time) {
    this.id = id;
    this.user_id = user_id;
    this.room_id = room_id;
    this.join_time = join_time;
    this.left_time = left_time;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Group group = (Group) obj;
      return ObjectsCompat.equals(getId(), group.getId()) &&
              ObjectsCompat.equals(getUserId(), group.getUserId()) &&
              ObjectsCompat.equals(getRoomId(), group.getRoomId()) &&
              ObjectsCompat.equals(getJoinTime(), group.getJoinTime()) &&
              ObjectsCompat.equals(getLeftTime(), group.getLeftTime()) &&
              ObjectsCompat.equals(getCreatedAt(), group.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), group.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getUserId())
      .append(getRoomId())
      .append(getJoinTime())
      .append(getLeftTime())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Group {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("user_id=" + String.valueOf(getUserId()) + ", ")
      .append("room_id=" + String.valueOf(getRoomId()) + ", ")
      .append("join_time=" + String.valueOf(getJoinTime()) + ", ")
      .append("left_time=" + String.valueOf(getLeftTime()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static UserIdStep builder() {
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
  public static Group justId(String id) {
    return new Group(
      id,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      user_id,
      room_id,
      join_time,
      left_time);
  }
  public interface UserIdStep {
    RoomIdStep userId(String userId);
  }
  

  public interface RoomIdStep {
    JoinTimeStep roomId(String roomId);
  }
  

  public interface JoinTimeStep {
    BuildStep joinTime(String joinTime);
  }
  

  public interface BuildStep {
    Group build();
    BuildStep id(String id);
    BuildStep leftTime(String leftTime);
  }
  

  public static class Builder implements UserIdStep, RoomIdStep, JoinTimeStep, BuildStep {
    private String id;
    private String user_id;
    private String room_id;
    private String join_time;
    private String left_time;
    public Builder() {
      
    }
    
    private Builder(String id, String user_id, String room_id, String join_time, String left_time) {
      this.id = id;
      this.user_id = user_id;
      this.room_id = room_id;
      this.join_time = join_time;
      this.left_time = left_time;
    }
    
    @Override
     public Group build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Group(
          id,
          user_id,
          room_id,
          join_time,
          left_time);
    }
    
    @Override
     public RoomIdStep userId(String userId) {
        Objects.requireNonNull(userId);
        this.user_id = userId;
        return this;
    }
    
    @Override
     public JoinTimeStep roomId(String roomId) {
        Objects.requireNonNull(roomId);
        this.room_id = roomId;
        return this;
    }
    
    @Override
     public BuildStep joinTime(String joinTime) {
        Objects.requireNonNull(joinTime);
        this.join_time = joinTime;
        return this;
    }
    
    @Override
     public BuildStep leftTime(String leftTime) {
        this.left_time = leftTime;
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
    private CopyOfBuilder(String id, String userId, String roomId, String joinTime, String leftTime) {
      super(id, user_id, room_id, join_time, left_time);
      Objects.requireNonNull(user_id);
      Objects.requireNonNull(room_id);
      Objects.requireNonNull(join_time);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
    }
    
    @Override
     public CopyOfBuilder roomId(String roomId) {
      return (CopyOfBuilder) super.roomId(roomId);
    }
    
    @Override
     public CopyOfBuilder joinTime(String joinTime) {
      return (CopyOfBuilder) super.joinTime(joinTime);
    }
    
    @Override
     public CopyOfBuilder leftTime(String leftTime) {
      return (CopyOfBuilder) super.leftTime(leftTime);
    }
  }
  

  public static class GroupIdentifier extends ModelIdentifier<Group> {
    private static final long serialVersionUID = 1L;
    public GroupIdentifier(String id) {
      super(id);
    }
  }
  
}
