package mc.apps.demo0.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashMap;

public class ViewModelFactory<T> implements ViewModelProvider.Factory {
    static HashMap<String, ViewModel> hashMapViewModel = new HashMap<String, ViewModel>();
    static void addViewModel(String key, ViewModel viewModel){
        hashMapViewModel.put(key, viewModel);
    }
    static ViewModel getViewModel(String key) {
        return hashMapViewModel.get(key);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if(modelClass.isAssignableFrom(MainViewModel.class)){
            String key = "UserProfileViewModel";
            if(hashMapViewModel.containsKey(key)){
                return (T)getViewModel(key);
            } else {
                addViewModel(key, new MainViewModel());
                return (T)getViewModel(key);
            }
        }
        return null;
    }
}
