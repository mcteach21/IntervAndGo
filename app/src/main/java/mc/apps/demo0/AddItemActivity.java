package mc.apps.demo0;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.List;

import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.User;
import mc.apps.demo0.ui.additem.AddItemFragment;
import mc.apps.demo0.viewmodels.MainViewModel;

public class AddItemActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final int SELECT_REQUEST_CODE_1 = 1000;
    private static final int SELECT_REQUEST_CODE_2 = 2000;
    private static final int CODE_CLIENT_SELECT = 1304;
    //private static final int CODE_USER_SELECT = 1404;

    MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item_activity);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        Intent intent = getIntent();
        int num = intent.getIntExtra("num", 1);
        defineFragment(num);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void defineFragment(int num) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, AddItemFragment.newInstance(num))
                .commitNow();
    }

    /**
     * Gestion calendrier
     */
    EditText edtDateTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    public void selectCalendarDateTime(View view) {

        edtDateTime = (EditText) view;

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mYear = year;
        mDay = dayOfMonth;
        mMonth = month;
        Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, this, mHour, mMinute, true);
        timePickerDialog.show();
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        edtDateTime.setText(mDay + "-" + (mMonth + 1) + "-" + mYear+" "+mHour+":"+mMinute);
    }


    /**
     * Boutons Click
     * @param view
     */
    public void list_clients_click(View view){
        startActivityForResult(new Intent(this, ClientsActivity.class), CODE_CLIENT_SELECT);
    }

    public void list_techs_click(View view){
        mainViewModel.clearSelected();

        Intent intent = new Intent(this, SelectActivity.class);
        intent.putExtra("num",1);
        startActivityForResult(intent, SELECT_REQUEST_CODE_1);
    }
    public void list_supervs_click(View view){
        Intent intent = new Intent(this, SelectActivity.class);
        intent.putExtra("num",2);
        startActivityForResult(intent, SELECT_REQUEST_CODE_2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SELECT_REQUEST_CODE_1 && data!=null){
                if(data.getSerializableExtra("data")!=null) {
                    List<User> selected = (List<User>) data.getSerializableExtra("data");
                    for (User u : selected)
                        mainViewModel.updateSelected(u, true);
                }
        }
        else  if(requestCode==SELECT_REQUEST_CODE_2 && data!=null){
                if(data.getSerializableExtra("data")!=null) {
                    User selected = (User) data.getSerializableExtra("data");
                    mainViewModel.setUser(selected);
                }
        }
        else if(requestCode==CODE_CLIENT_SELECT){
            if(data!=null)
                if(data.getSerializableExtra("data")!=null) {
                    Client selected = (Client) data.getSerializableExtra("data");
                    mainViewModel.setClient(selected);
                }
        }
       /* else if(requestCode==CODE_USER_SELECT){
            if(data!=null)
                if (data.getSerializableExtra("data") != null) {
                    User selected = (User) data.getSerializableExtra("data");
                    edtSearchSupervisor.setText(selected.getCode());
                }
        }*/
    }
}