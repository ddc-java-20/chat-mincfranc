package edu.cnm.deepdive.chat.viewmodel;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.chat.model.dto.Channel;
import edu.cnm.deepdive.chat.model.dto.Message;
import edu.cnm.deepdive.chat.service.MessageService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

@HiltViewModel
public class MessageViewModel extends ViewModel implements DefaultLifecycleObserver {

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
    messages = new MutableLiveData<>();
    channels = new MutableLiveData<>();
    selectedChannel = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
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
      // TODO: 3/19/2025 Start a new query for messages in the selected channel. 2 housekeeping methods,
      //and methods for UI to invoke to post a message
      selectedChannel.setValue(channel);
    }
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }


}
