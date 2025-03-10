package edu.cnm.deepdive.chat.service;

import edu.cnm.deepdive.chat.model.entity.Message;
import edu.cnm.deepdive.chat.model.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AbstractMessageService {

  List<Message> add(Message message, UUID channelKey, User author, Instant since);

  List<Message> getSince(UUID channelKey, Instant since);

}
