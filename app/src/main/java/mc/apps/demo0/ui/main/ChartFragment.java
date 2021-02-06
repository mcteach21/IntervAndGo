package mc.apps.demo0.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;

import java.util.ArrayList;
import java.util.List;

import mc.apps.demo0.ChartActivity;
import mc.apps.demo0.R;
import mc.apps.demo0.dao.StatsDao;
import mc.apps.demo0.model.ItemTotal;
import mc.apps.demo0.viewmodels.MainViewModel;

public class ChartFragment extends Fragment {
    private MainViewModel mainViewModel;
    private static int num;

    public static ChartFragment newInstance(int num) {
        ChartFragment.num = num;
        return new ChartFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        int layout = (num==1)?R.layout.chart_main_1_fragment:R.layout.chart_main_2_fragment;
        return inflater.inflate(layout, container, false);
    }

    View root;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.root = view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        initPieChart();
    }
    private void initPieChart() {

        AnyChartView anyChartView = root.findViewById(R.id.pie_chart_view);
        anyChartView.setProgressBar(root.findViewById(R.id.progress_bar));

        Pie pie = AnyChart.pie();
        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(getActivity(), event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });

        //pie.title("Statistiques");
        pie.labels().position("outside");
        pie.legend().title().enabled(true);

        String title = (num==1)?"Interventions par Client":"Interventions par Technicien";

        pie.legend().title().text(title).padding(0d, 0d, 10d, 0d);
        pie.legend().position("center-bottom").itemsLayout(LegendLayout.HORIZONTAL).align(Align.CENTER);

        StatsDao dao = new StatsDao();
        dao.ItemTotalStats(num, (items_, message)->{
            List<ItemTotal> items = dao.Deserialize(items_, ItemTotal.class);
            List<DataEntry> data = new ArrayList<>();

            for (ItemTotal item : items) {
                data.add(new ValueDataEntry(item.getCode(), item.getTotal()));
                Log.i("tests" , "initPieChart: " + item.getCode() + " " + item.getTotal());
            }
            pie.data(data);

            //anyChartView.invalidate();
            anyChartView.setChart(pie);
        });
    }

}