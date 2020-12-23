package mc.apps.demo0.ui.rapports;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RapportsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RapportsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Gestion Rapports..");
    }

    public LiveData<String> getText() {
        return mText;
    }
}