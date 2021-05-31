package mc.apps.interv.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import mc.apps.interv.model.Client;
import mc.apps.interv.model.Intervention;
import mc.apps.interv.model.User;

public class MainViewModel  extends ViewModel {

    private MutableLiveData<Serializable> item = new MutableLiveData<>();
    public MutableLiveData<Serializable> getItem() { return item; }
    public void setItem(Serializable value) { item.setValue(value); }

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

    private MutableLiveData<List<Uri>> signatures_images = new MutableLiveData<>();
    public MutableLiveData<List<Uri>> getSignaturesImages() {
        return signatures_images;
    }
    public void addSignatureImage(Uri image) {
        List<Uri> values = signatures_images.getValue();
        if(values==null)
            values = new ArrayList();
        values.add(image);
        signatures_images.setValue(values);
    }
    public void resetSignaturesImages() {
        signatures_images.setValue(new ArrayList());
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

    //current supervisor code
    private MutableLiveData<String> supervisor = new MutableLiveData<>();
    public MutableLiveData<String> getSupervisor() { return supervisor; }
    public void setSupervisor(String value) {  supervisor.setValue(value);  }

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
