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

/** This is an auto generated class representing the Message type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Messages", type = Model.Type.USER, version = 1)
public final class Message implements Model {
  public static final QueryField ID = field("Message", "id");
  public static final QueryField FROM_ID = field("Message", "from_id");
  public static final QueryField FROM_NAME = field("Message", "from_name");
  public static final QueryField TEXT = field("Message", "text");
  public static final QueryField DATETIME = field("Message", "datetime");
  public static final QueryField ROOM_ID = field("Message", "room_id");
  public static final QueryField TYPE = field("Message", "type");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String from_id;
  private final @ModelField(targetType="String") String from_name;
  private final @ModelField(targetType="String", isRequired = true) String text;
  private final @ModelField(targetType="String", isRequired = true) String datetime;
  private final @ModelField(targetType="String", isRequired = true) String room_id;
  private final @ModelField(targetType="String", isRequired = true) String type;
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
  
  public String getFromId() {
      return from_id;
  }
  
  public String getFromName() {
      return from_name;
  }
  
  public String getText() {
      return text;
  }
  
  public String getDatetime() {
      return datetime;
  }
  
  public String getRoomId() {
      return room_id;
  }
  
  public String getType() {
      return type;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Message(String id, String from_id, String from_name, String text, String datetime, String room_id, String type) {
    this.id = id;
    this.from_id = from_id;
    this.from_name = from_name;
    this.text = text;
    this.datetime = datetime;
    this.room_id = room_id;
    this.type = type;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Message message = (Message) obj;
      return ObjectsCompat.equals(getId(), message.getId()) &&
              ObjectsCompat.equals(getFromId(), message.getFromId()) &&
              ObjectsCompat.equals(getFromName(), message.getFromName()) &&
              ObjectsCompat.equals(getText(), message.getText()) &&
              ObjectsCompat.equals(getDatetime(), message.getDatetime()) &&
              ObjectsCompat.equals(getRoomId(), message.getRoomId()) &&
              ObjectsCompat.equals(getType(), message.getType()) &&
              ObjectsCompat.equals(getCreatedAt(), message.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), message.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getFromId())
      .append(getFromName())
      .append(getText())
      .append(getDatetime())
      .append(getRoomId())
      .append(getType())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Message {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("from_id=" + String.valueOf(getFromId()) + ", ")
      .append("from_name=" + String.valueOf(getFromName()) + ", ")
      .append("text=" + String.valueOf(getText()) + ", ")
      .append("datetime=" + String.valueOf(getDatetime()) + ", ")
      .append("room_id=" + String.valueOf(getRoomId()) + ", ")
      .append("type=" + String.valueOf(getType()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static FromIdStep builder() {
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
  public static Message justId(String id) {
    return new Message(
      id,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      from_id,
      from_name,
      text,
      datetime,
      room_id,
      type);
  }
  public interface FromIdStep {
    TextStep fromId(String fromId);
  }
  

  public interface TextStep {
    DatetimeStep text(String text);
  }
  

  public interface DatetimeStep {
    RoomIdStep datetime(String datetime);
  }
  

  public interface RoomIdStep {
    TypeStep roomId(String roomId);
  }
  

  public interface TypeStep {
    BuildStep type(String type);
  }
  

  public interface BuildStep {
    Message build();
    BuildStep id(String id);
    BuildStep fromName(String fromName);
  }
  

  public static class Builder implements FromIdStep, TextStep, DatetimeStep, RoomIdStep, TypeStep, BuildStep {
    private String id;
    private String from_id;
    private String text;
    private String datetime;
    private String room_id;
    private String type;
    private String from_name;
    public Builder() {
      
    }
    
    private Builder(String id, String from_id, String from_name, String text, String datetime, String room_id, String type) {
      this.id = id;
      this.from_id = from_id;
      this.from_name = from_name;
      this.text = text;
      this.datetime = datetime;
      this.room_id = room_id;
      this.type = type;
    }
    
    @Override
     public Message build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Message(
          id,
          from_id,
          from_name,
          text,
          datetime,
          room_id,
          type);
    }
    
    @Override
     public TextStep fromId(String fromId) {
        Objects.requireNonNull(fromId);
        this.from_id = fromId;
        return this;
    }
    
    @Override
     public DatetimeStep text(String text) {
        Objects.requireNonNull(text);
        this.text = text;
        return this;
    }
    
    @Override
     public RoomIdStep datetime(String datetime) {
        Objects.requireNonNull(datetime);
        this.datetime = datetime;
        return this;
    }
    
    @Override
     public TypeStep roomId(String roomId) {
        Objects.requireNonNull(roomId);
        this.room_id = roomId;
        return this;
    }
    
    @Override
     public BuildStep type(String type) {
        Objects.requireNonNull(type);
        this.type = type;
        return this;
    }
    
    @Override
     public BuildStep fromName(String fromName) {
        this.from_name = fromName;
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
    private CopyOfBuilder(String id, String fromId, String fromName, String text, String datetime, String roomId, String type) {
      super(id, from_id, from_name, text, datetime, room_id, type);
      Objects.requireNonNull(from_id);
      Objects.requireNonNull(text);
      Objects.requireNonNull(datetime);
      Objects.requireNonNull(room_id);
      Objects.requireNonNull(type);
    }
    
    @Override
     public CopyOfBuilder fromId(String fromId) {
      return (CopyOfBuilder) super.fromId(fromId);
    }
    
    @Override
     public CopyOfBuilder text(String text) {
      return (CopyOfBuilder) super.text(text);
    }
    
    @Override
     public CopyOfBuilder datetime(String datetime) {
      return (CopyOfBuilder) super.datetime(datetime);
    }
    
    @Override
     public CopyOfBuilder roomId(String roomId) {
      return (CopyOfBuilder) super.roomId(roomId);
    }
    
    @Override
     public CopyOfBuilder type(String type) {
      return (CopyOfBuilder) super.type(type);
    }
    
    @Override
     public CopyOfBuilder fromName(String fromName) {
      return (CopyOfBuilder) super.fromName(fromName);
    }
  }
  

  public static class MessageIdentifier extends ModelIdentifier<Message> {
    private static final long serialVersionUID = 1L;
    public MessageIdentifier(String id) {
      super(id);
    }
  }
  
}
