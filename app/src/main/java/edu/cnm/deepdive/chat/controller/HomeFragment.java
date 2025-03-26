package edu.cnm.deepdive.chat.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.chat.R;
import edu.cnm.deepdive.chat.databinding.FragmentHomeBinding;
import edu.cnm.deepdive.chat.model.dto.Channel;
import edu.cnm.deepdive.chat.model.dto.Message;
import edu.cnm.deepdive.chat.viewmodel.LoginViewModel;
import edu.cnm.deepdive.chat.viewmodel.MessageViewModel;
import java.util.List;

//the only reason we need ViewModel is bec we need it to sign out

/**
 * @noinspection SequencedCollectionMethodCanBeUsed
 */
@AndroidEntryPoint
public class HomeFragment extends Fragment implements MenuProvider, OnItemSelectedListener {

  private static final String TAG = HomeFragment.class.getSimpleName();

  private FragmentHomeBinding binding;
  private LoginViewModel loginViewModel;
  private MessageViewModel messageViewModel;
  private Channel selectedChannel;
  private List<Channel> channels;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentHomeBinding.inflate(inflater, container, false);
    // TODO: 3/19/2025 Attach listener to send button, so that when clicked, a new message instance
    //  is created and passed to messageViewModel.
    binding.channels.setOnItemSelectedListener(this);
    binding.send.setOnClickListener((v) -> {
      Message message = new Message();
      //noinspection DataFlowIssue
      message.setText(binding.message.getText().toString().strip());
      messageViewModel.sendMessage(message);
      binding.message.setText("");
    });
    return binding.getRoot();
  }

  //only when we get a null account, will we navigate back to pre login fragment
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    LifecycleOwner owner = getViewLifecycleOwner();
    setupLoginViewModel(owner);
    setupMessageViewModel(owner);
    requireActivity().addMenuProvider(this, owner, State.RESUMED);
  }


  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  @Override
  public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
    menuInflater.inflate(R.menu.home_options, menu);
  }

  //we have to return a boolean saying if we handled the menu selection
  @Override
  public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
    boolean handled = false;
    if (menuItem.getItemId() == R.id.sign_out) {
      loginViewModel.signOut();
      handled = true;
    }
    return handled;
  }


  private void setupLoginViewModel(LifecycleOwner owner) {
    loginViewModel = new ViewModelProvider(requireActivity())
        .get(LoginViewModel.class);
    loginViewModel
        .getAccount()
        .observe(owner, (account) -> {
          if (account != null) {
            Log.d(TAG, "Bearer " + account.getIdToken());
          } else {
            Navigation.findNavController(binding.getRoot())
                .navigate(HomeFragmentDirections.navigateToPreLoginFragment());
          }
        });
  }

  private void setupMessageViewModel(LifecycleOwner owner) {
    messageViewModel = new ViewModelProvider(this)
        .get(MessageViewModel.class);
    getLifecycle().addObserver(messageViewModel);
    messageViewModel
        .getChannels()
        .observe(owner, (channels) -> {
          this.channels = channels;
          // Array adapter attached to a spinner(drop down menu) to display/inflate the channels.
          ArrayAdapter<Channel> adapter = new ArrayAdapter<>(requireContext(),
              android.R.layout.simple_list_item_1, channels);
          binding.channels.setAdapter(adapter);
          setChannelSelection();
        });
    messageViewModel
        .getMessages()
        .observe(owner, (messages) -> {
          // TODO: 3/19/2025 pass data to recyclerview adapter, and notify adapter that the data has changed.
          // TODO: 3/19/2025 Scroll so that the most recent message is visible.
        });
    messageViewModel
        .getSelectedChannel()
        .observe(owner, (channel) -> {
          selectedChannel = channel;
          setChannelSelection();
        });
  }

  private void setChannelSelection() {
    if (channels != null && selectedChannel != null) {
      int position = channels.indexOf(selectedChannel);
      if (position >= 0) {
        binding.channels.setSelection(position, true);
      }

    }
  }

  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    Channel channel = (Channel) parent.getItemAtPosition(position);
    messageViewModel.setSelectedChannel(channel);
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {

  }
}
