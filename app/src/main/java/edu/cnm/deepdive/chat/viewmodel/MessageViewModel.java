package edu.cnm.deepdive.chat.viewmodel;

import android.util.Log;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.chat.model.dto.Channel;
import edu.cnm.deepdive.chat.model.dto.Message;
import edu.cnm.deepdive.chat.service.MessageService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

@HiltViewModel
public class MessageViewModel extends ViewModel implements DefaultLifecycleObserver {

  private static final String TAG = MessageViewModel.class.getSimpleName();

  //FIRST create fields to: reference to message service, to get all messages for a particular channel
//as we make asynchronous requests and UI has received messages, then empty the jobs requests bucket
  private final MessageService messageService;
  private final MutableLiveData<List<Message>> messages;
  private final MutableLiveData<List<Channel>> channels;
  private final MutableLiveData<Channel> selectedChannel;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  //messageService will be the only thing Hilt will take in from the outside with this constructor
  @Inject
  public MessageViewModel(MessageService messageService) {
    this.messageService = messageService;
    messages = new MutableLiveData<>(new LinkedList<>());
    channels = new MutableLiveData<>();
    selectedChannel = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
    fetchChannels();
  }

  //getters for variables with LiveData, remove Mutable because we want the UI to observe and not have
// access to changing the LiveData
  public LiveData<List<Channel>> getChannels() {
    return channels;
  }

  public LiveData<List<Message>> getMessages() {
    return messages;
  }

  public LiveData<Channel> getSelectedChannel() {
    return selectedChannel;
  }

  //method UI can call to select LiveData, when we get new channel- if it's different from the one
  // being passed in (Channel channel), we clear our messages and write a new query
  //look at dto>channel ******
  public void setSelectedChannel(@NotNull Channel channel) {
    if (!channel.equals(selectedChannel.getValue())) {
      messages.setValue(new LinkedList<>());
      selectedChannel.setValue(channel);
      fetchMessages();
    }
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

//the UI should be able to  fetch a channel, fetch a message, change a channel, post a channel, add a message to a channel

  public void fetchChannels() {
    messageService
        .getChannels(true)
        .subscribe(
            channels::postValue,
            this::postThrowable,
            pending
        );
  }

  //Ternary, if the list is empty, fetch since the last time they were fetched(?), else get the last posted (:)
  //Instant.MIN= the last 30 minutes per messageService in server side- getEffectiveSince(MAX= 30 MIN)

  /**
   * @noinspection DataFlowIssue
   */
  public void fetchMessages() {
    throwable.postValue(null);
    List<Message> messages = this.messages.getValue();
    //noinspection SequencedCollectionMethodCanBeUsed,DataFlowIssue
    Instant since = getSince(messages);
    //this gets a piece of machinery<first 4 parts on board diagram of asynch process>
    //subscribe - provides consumer of a successful result, of an unsuccessful result & pending
    messageService
        .getMessages(selectedChannel.getValue().getKey(), since)
        .subscribe(
            (msgs) -> {
              messages.addAll(msgs);
              this.messages.postValue(messages);
              fetchChannels();   //successful result consumer that is called at other points and times, not here, not recursive
            },
            this::postThrowable, //unsuccessful result consumer
            pending
        );
  }

  /**
   * @noinspection DataFlowIssue
   */ //list of a bunch of machines in a row, then ignoreElement turns it into a completion event
  //when we subscribe to a completable we don't have a consumer
  //then put ticket in pending bucket
  public void sendMessage(Message message) {
    throwable.setValue(null);
    Instant since = getSince(messages.getValue());
    messageService
        .sendMessage(selectedChannel.getValue().getKey(), message, since)
        .ignoreElement()
        .subscribe(
            () -> {
            },
            this::postThrowable,
            pending
        );
  }

  @Override
  public void onResume(@NotNull LifecycleOwner owner) {
    DefaultLifecycleObserver.super.onResume(owner);
    if (selectedChannel.getValue() != null) {
      fetchChannels();
    }
    ;
  }

  //adding a lifecycle observer in the UI model of a UI controller if we have an is-a relationship
//we're clearing container contents BUT NOT deleting container, we will use container for new content
  @Override
  public void onStop(@NotNull LifecycleOwner owner) {
    pending.clear();
    DefaultLifecycleObserver.super.onStop(owner);
  }

  private static Instant getSince(List<Message> messages) {
    //noinspection SequencedCollectionMethodCanBeUsed
    return messages.isEmpty()
        ? Instant.MIN
        : messages
            .get(messages.size() - 1)
            .getPosted();
  }

  //throwable is the parameter, this.throwable is the value of the field which the parameter refers to
  private void postThrowable(Throwable throwable) {
    Log.e(TAG, throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }


}
