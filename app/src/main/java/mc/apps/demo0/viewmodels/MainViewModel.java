package mc.apps.demo0.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import mc.apps.demo0.model.User;

public class MainViewModel  extends ViewModel {
    //private MutableLiveData<String> title = new MutableLiveData<>();
    private MutableLiveData<String> search = new MutableLiveData<>();
    private MutableLiveData<List<User>> selected = new MutableLiveData<>();

    public MutableLiveData<String> getSearch() {
        return search;
    }
    public void setSearch(String value) {
        search.setValue(value);
    }

    public void updateSelected(User user, boolean add){
        if(selected.getValue()==null)
            selected.setValue(new ArrayList<>());

        List<User> values = selected.getValue();
        if(add)
            values.add(user);
        else
            values.remove(user);

        selected.setValue(values);
        Log.i("tests", "Selected: "+selected.getValue());
    }
    public MutableLiveData<List<User>> getSelected() {
        return selected;
    }
}
