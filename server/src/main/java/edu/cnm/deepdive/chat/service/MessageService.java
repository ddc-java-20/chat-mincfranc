package edu.cnm.deepdive.chat.service;

import edu.cnm.deepdive.chat.model.dao.ChannelRepository;
import edu.cnm.deepdive.chat.model.dao.MessageRepository;
import edu.cnm.deepdive.chat.model.entity.Channel;
import edu.cnm.deepdive.chat.model.entity.Message;
import edu.cnm.deepdive.chat.model.entity.User;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService implements AbstractMessageService {

  private static final Duration MAX_SINCE_DURATION = Duration.ofMinutes(30);

  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;

  @Autowired
  public MessageService(MessageRepository messageRepository, ChannelRepository channelRepository) {
    this.messageRepository = messageRepository;
    this.channelRepository = channelRepository;
  }

  //these are 2 main business logic methods: add & getSince. Details are in the helper methods
  @Override
  public List<Message> add(Message message, UUID channelKey, User author, Instant since) {
    return channelRepository
        .findByExternalKey(channelKey)
        .map((channel) -> addAndRefresh(message, author, since, channel))
        .orElseThrow();
  }

  @Override
  public List<Message> getSince(UUID channelKey, Instant since) {
    return channelRepository
        .findByExternalKey(channelKey)
        .map((channel) -> getSinceAtMost(since, channel))
        .orElseThrow();
  }

  private List<Message> getSinceAtMost(Instant since, Channel channel) {
    Instant effectiveSince = getEffectiveSince(since);
    return messageRepository
        .getAllByChannelAndPostedAfterOrderByPostedAsc(channel, effectiveSince);
  }

  private List<Message> addAndRefresh(
      Message message, User author, Instant since, Channel channel) {
    message.setChannel(channel);
    message.setSender(author);
    messageRepository.save(message);
    Instant effectiveSince = getEffectiveSince(since);
    return messageRepository
        .getAllByChannelAndPostedAfterOrderByPostedAsc(channel, effectiveSince);
  }
  private static Instant getEffectiveSince(Instant since) {
    Instant earliestSince = Instant.now().minus(MAX_SINCE_DURATION);
    return (since.isBefore(earliestSince)) ? earliestSince : since;
  }

}
