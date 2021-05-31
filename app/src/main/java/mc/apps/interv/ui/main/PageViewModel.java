package mc.apps.interv.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();

    String[] titles = {"Interventions", "Planifier Intervention", "Infos & Statistiques"};
    private LiveData<String> mText = Transformations.map(mIndex, input -> titles[input-1]);

    public void setIndex(int index) {
        mIndex.setValue(index);
    }
    public LiveData<String> getText() {
        return mText;
    }
}