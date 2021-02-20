package mc.apps.demo0;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mc.apps.demo0.adapters.ClientsAdapter;
import mc.apps.demo0.adapters.SelectedUsersAdapter;
import mc.apps.demo0.adapters.UsersAdapter;
import mc.apps.demo0.dao.ClientDao;
import mc.apps.demo0.dao.UserDao;
import mc.apps.demo0.model.Client;
import mc.apps.demo0.model.User;
import mc.apps.demo0.viewmodels.MainViewModel;

public class SearchActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "tests";
    private static final int REQUEST_FILTRE_CODE = 1603;
    private static final int SELECT_REQUEST_CODE = 2608 ;
    private static final int CODE_CLIENT_SELECT = 1304;
    private static final int CODE_USER_SELECT = 1404;
    private   MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        InitFilter();

        Button btn = findViewById(R.id.btn_filter_ok);
        btn.setOnClickListener(v->{
            applyFilter();
        });
        initListTech();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    TextView txtSearchCodeClient;
    EditText edtSearchSupervisor, edtDateDebutPrev, edtDateDebutReel;
    RecyclerView search_tech_list;
    RadioGroup statusChoice;

    private void InitFilter() {
        txtSearchCodeClient = findViewById(R.id.txtSearchCodeClient);
        edtSearchSupervisor = findViewById(R.id.edtSearchSupervisor);
        search_tech_list = findViewById(R.id.search_tech_list);
        statusChoice = findViewById(R.id.statusChoice);
        edtDateDebutPrev = findViewById(R.id.edtDateDebutPrev);
        edtDateDebutReel = findViewById(R.id.edtDateDebutReel);
    }


    private void applyFilter() {
        //appliquer filtre!

        String codeClient = txtSearchCodeClient.getText().toString();
        String codeSupervisor = edtSearchSupervisor.getText().toString();

        String dateDebutPrev = edtDateDebutPrev.getText().toString();
        String dateDebutReel = edtDateDebutReel.getText().toString();

        int checkedID = statusChoice.getCheckedRadioButtonId();
        int status = (checkedID==R.id.radioButton1)?1:(checkedID==R.id.radioButton2)?2:(checkedID==R.id.radioButton3)?5:0;

        Intent intent=new Intent();

        intent.putExtra("codeClient", codeClient);
        intent.putExtra("codeSupervisor", codeSupervisor);
        intent.putExtra("dateDebutPrev", dateDebutPrev);
        intent.putExtra("dateDebutReel", dateDebutReel);
        intent.putExtra("status", status);

        List<User> technicians = ((SelectedUsersAdapter)search_tech_list.getAdapter()).getItems();
        ArrayList<String> codesTechnicians = new ArrayList<String>();
        if(technicians.size()>0){
            for (User tech: technicians )
                codesTechnicians.add(tech.getCode());
            intent.putStringArrayListExtra("codesTechnicians", codesTechnicians);
        }

        setResult(REQUEST_FILTRE_CODE, intent);
        finish();
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

        edtDateTime.setText(mDay + "-" + (mMonth + 1) + "-" + mYear);

       /* TimePickerDialog timePickerDialog = new TimePickerDialog(this, this, mHour, mMinute, true);
        timePickerDialog.show();*/
    }
    /*@Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        edtDateTime.setText(mDay + "-" + (mMonth + 1) + "-" + mYear+" "+mHour+":"+mMinute);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SELECT_REQUEST_CODE){
            if(data!=null)
                if(data.getSerializableExtra("data")!=null) {
                    List<User> selected = (List<User>) data.getSerializableExtra("data");
                    for (User u : selected)
                        mainViewModel.updateSelected(u, true);
                }
        }
        else if(requestCode==CODE_CLIENT_SELECT){
            if(data!=null)
                if(data.getSerializableExtra("data")!=null) {
                    Client selected = (Client) data.getSerializableExtra("data");
                    txtSearchCodeClient.setText(selected.getCode());
                }
        }
        else if(requestCode==CODE_USER_SELECT){
            if(data!=null)
                if (data.getSerializableExtra("data") != null) {
                    User selected = (User) data.getSerializableExtra("data");
                    edtSearchSupervisor.setText(selected.getCode());
                }
        }
    }

    public void btnSearchFromList(View view) {
        int id = view.getId();
        switch(id){
            case R.id.btn_search_clients_list:
                startActivityForResult(new Intent(this, ClientsActivity.class), CODE_CLIENT_SELECT);
                break;
            case R.id.btn_search_supervs_list:
                Intent intent = new Intent(this, UsersActivity.class);
                intent.putExtra("profil",2);            //filtre sur superviseurs
                startActivityForResult(intent, CODE_USER_SELECT);
                break;
            case R.id.btn_search_techs_list:
                mainViewModel.clearSelected();
                startActivityForResult(new Intent(this, SelectActivity.class), SELECT_REQUEST_CODE);
                break;
        }
    }

    RecyclerView tech_list;
    List<User> selected = new ArrayList();
    private void initListTech(){
        tech_list = findViewById(R.id.search_tech_list);
        tech_list.setHasFixedSize(true);
        GridLayoutManager layoutManager2 = new GridLayoutManager(this, 2);
        tech_list.setLayoutManager(layoutManager2);

        SelectedUsersAdapter adapter = new SelectedUsersAdapter(
                selected,
                null,
                true
        );
        tech_list.setAdapter(adapter);
        mainViewModel.getSelected().observe(this, selected -> {
            adapter.refresh(selected);
        });
    }

}