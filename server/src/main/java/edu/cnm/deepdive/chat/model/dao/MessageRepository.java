package edu.cnm.deepdive.chat.model.dao;

import edu.cnm.deepdive.chat.model.entity.Channel;
import edu.cnm.deepdive.chat.model.entity.Message;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
//this is key query to refresh display, recurrently q20 sec
  List<Message> getAllByChannelAndPostedAfterOrderByPostedAsc(Channel channel, Instant posted);

}


//we never need to retrieve a message by its external key
//when the controller receives a request to fetch messages since a date instance, if there aren't, it will wait 20 sec until there are some