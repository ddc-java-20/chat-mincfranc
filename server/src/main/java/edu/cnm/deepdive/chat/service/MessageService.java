package edu.cnm.deepdive.chat.service;

import edu.cnm.deepdive.chat.model.dao.ChannelRepository;
import edu.cnm.deepdive.chat.model.dao.MessageRepository;
import edu.cnm.deepdive.chat.model.entity.Channel;
import edu.cnm.deepdive.chat.model.entity.Message;
import edu.cnm.deepdive.chat.model.entity.User;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

@Service
public class MessageService implements AbstractMessageService {

  private static final Duration MAX_SINCE_DURATION = Duration.ofMinutes(30);
  private static final Long POLLING_TIMEOUT_MS = 20_000L;
  private static final int POLLING_POOL_SIZE = 4;

  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final ScheduledExecutorService scheduler;


  @Autowired
  public MessageService(MessageRepository messageRepository, ChannelRepository channelRepository) {
    this.messageRepository = messageRepository;
    this.channelRepository = channelRepository;
    scheduler = Executors.newScheduledThreadPool(POLLING_POOL_SIZE)
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

  @Override
  public DeferredResult<List<Message>> pullSince(UUID channelKey, Instant since) {
    DeferredResult<List<Message>> result = new DeferredResult<>(POLLING_TIMEOUT_MS);
    ScheduledFuture<?>[] future = new ScheduledFuture<?>[1];
    result.onTimeout(() -> result.setResult(List.of()));
    Runnable runnable = () -> {
      if (!messageRepository.getAllByChannelAndPostedAfterOrderByPostedAsc(, since).isEmpty()) {
        result.setResult(
            messageRepository.getAllByChannelAndPostedAfterOrderByPostedAsc(, since));
        future[0].cancel(true);
      }
    };
    //This is the first delay before it starts. Runnable is a task, invoke its run method AFTER the
    // first 2 seconds elapsed, then 2 sec after. It will do that until we cancel it.
    future[0] = scheduler.scheduleWithFixedDelay(runnable, 2000L, 2000L, TimeUnit.MILLISECONDS);
    return result;
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
