package mc.apps.demo0.viewmodels;

import android.net.Uri;
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

    private MutableLiveData<List<Uri>> images = new MutableLiveData<>();

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

    public void clearSelected() {
        selected.setValue(new ArrayList<>());
    }

    public MutableLiveData<List<Uri>> getImages() {
        return images;
    }

    public void addImage(Uri image) {
        List<Uri> values = images.getValue();
        if(values==null)
            values = new ArrayList();
        values.add(image);
        images.setValue(values);
    }
}
