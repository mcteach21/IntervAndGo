package mc.apps.demo0.ui.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            String txt = (input==1)?"Interventions":"Planifier Intervention";
            return txt;
        }
    });

    public void setIndex(int index) {
        mIndex.setValue(index);
    }
    public LiveData<String> getText() {
        return mText;
    }
}