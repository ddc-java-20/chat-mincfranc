package edu.cnm.deepdive.chat.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import edu.cnm.deepdive.chat.viewmodel.LoginViewModel;
import edu.cnm.deepdive.chat.viewmodel.MessageViewModel;

//the only reason we need ViewModel is bec we need it to sign out

@AndroidEntryPoint
public class HomeFragment extends Fragment implements MenuProvider {

  private static final String TAG = HomeFragment.class.getSimpleName();

  private FragmentHomeBinding binding;
  private LoginViewModel loginViewModel;
  private MessageViewModel messageViewModel;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentHomeBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  //only when we get a null account, will we navigate back to pre login fragment
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    loginViewModel = new ViewModelProvider(requireActivity())
        .get(LoginViewModel.class);
    LifecycleOwner owner = getViewLifecycleOwner();
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
    messageViewModel = new ViewModelProvider(this)
        .get(MessageViewModel.class);
    getLifecycle().addObserver(messageViewModel);
    messageViewModel
        .getChannels()
            .observe(owner, (channels) -> {
              // TODO: 3/19/2025 Attach an array adapter to a spinner to display the channels.
              Log.d(TAG, "Channels " + channels);
            });
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
      handled= true;
    }
    return handled;
  }

}
