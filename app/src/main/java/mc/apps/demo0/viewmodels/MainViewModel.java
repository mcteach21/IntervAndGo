package mc.apps.demo0.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
public class MainViewModel  extends ViewModel {
    //private MutableLiveData<String> title = new MutableLiveData<>();
    private MutableLiveData<String> search = new MutableLiveData<>();

    public MutableLiveData<String> getSearch() {
        return search;
    }
    public void setSearch(String value) {
        search.setValue(value);
    }

    /*public MutableLiveData<String> getTitle() { return title; }
    public void setTitle(String value) { title.setValue(value); }*/
}
