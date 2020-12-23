package mc.apps.demo0.ui.interventions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InterventionsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public InterventionsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Gestion Interventions..");
    }

    public LiveData<String> getText() {
        return mText;
    }
}