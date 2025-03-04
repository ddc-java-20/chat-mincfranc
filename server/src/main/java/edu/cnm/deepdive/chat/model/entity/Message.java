package edu.cnm.deepdive.chat.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
public class Message {

  @Id
  @GeneratedValue
  @Column(name = "message_id", nullable = false, updatable = false)
  private long id;

  @Column(nullable = false, updatable = false, unique = true)
  private UUID externalKey;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Instant posted;

  @Column(nullable = false, updatable = false, length = 255)
  private String text;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "sender_id", nullable = false, updatable = false)
  private User sender;   //a field that foreign key uses to reference, and the annotation to say what  the column will be names

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "channel_id", nullable = false, updatable = false)
  private Channel channel;



}
