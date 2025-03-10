package edu.cnm.deepdive.chat.service;

import edu.cnm.deepdive.chat.model.dao.ChannelRepository;
import edu.cnm.deepdive.chat.model.dao.MessageRepository;
import edu.cnm.deepdive.chat.model.entity.Channel;
import edu.cnm.deepdive.chat.model.entity.Message;
import edu.cnm.deepdive.chat.model.entity.User;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService implements AbstractMessageService {


  private ChannelRepository channelRepository;
  private MessageRepository messageRepository;

  @Autowired
  public MessageService(MessageRepository messageRepository, ChannelRepository channelRepository) {
    this.messageRepository = messageRepository;
    this.channelRepository = channelRepository;
  }


  @Override
  public List<Message> add(Message message, UUID channelKey, User author, Instant since) {
    return channelRepository
        .findByExternalKey(channelKey)
        .map((channel) ->{
          message.setChannel(channel);
          message.setSender(author);
          messageRepository.save(message);
          return messageRepository
              .getAllByChannelAndPostedAfterOrderByPostedAsc(channel, since);
        })
        .orElseThrow();
  }

  @Override
  public List<Message> getSince(UUID channelKey, Instant since) {
    return channelRepository
        .findByExternalKey(channelKey)
        .map((channel) ->
            messageRepository.getAllByChannelAndPostedAfterOrderByPostedAsc(channel, since))
        .orElseThrow();
  }
}
