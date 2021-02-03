package mc.apps.demo0.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.Intervention;
import mc.apps.demo0.model.User;

public class MainViewModel  extends ViewModel {
    private MutableLiveData<String> demo = new MutableLiveData<>();
    public MutableLiveData<String> getDemo() { return demo; }
    public void setDemo(String value) { demo.setValue(value); }

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

    private MutableLiveData<Hashtable<String, Object>> filter = new MutableLiveData<>();
    public MutableLiveData<Hashtable<String, Object>> getFilter() {
        return filter;
    }
    public void setFilter(Hashtable<String, Object> value) {
        filter.setValue(value);
    }

    private MutableLiveData<Client> client = new MutableLiveData<>();
    public MutableLiveData<Client> getClient() { return client; }
    public void setClient(Client value) {  client.setValue(value);  }

    private MutableLiveData<User> user = new MutableLiveData<>();
    public MutableLiveData<User> getUser() { return user; }
    public void setUser(User value) {  user.setValue(value);  }

    private MutableLiveData<Integer> profil = new MutableLiveData<>();
    public MutableLiveData<Integer> getProfil() { return profil; }
    public void setProfil(int value) {  profil.setValue(value);  }

    private MutableLiveData<Intervention> intervention = new MutableLiveData<>();
    public MutableLiveData<Intervention> getIntervention() { return intervention; }
    public void setIntervention(Intervention value) {  intervention.setValue(value);  }

    private MutableLiveData<Integer> num = new MutableLiveData<>();
    public MutableLiveData<Integer> getNum() { return num; }
    public void setNum(int value) {  num.setValue(value);  }

    private MutableLiveData<Boolean> refresh = new MutableLiveData<>();
    public MutableLiveData<Boolean> getRefresh() { return refresh; }
    public void setRefresh(boolean value) {  refresh.setValue(value);  }
}
