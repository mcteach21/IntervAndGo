package mc.apps.demo0.ui.comptes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ComptesViewModel extends ViewModel {
    private MutableLiveData<String> mText;
    public ComptesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Gestion Comptes..");
    }
    public LiveData<String> getText() {
        return mText;
    }
}