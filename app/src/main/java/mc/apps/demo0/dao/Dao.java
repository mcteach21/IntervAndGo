package mc.apps.demo0.dao;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Dao<T> {
    private static final String TAG = "tests" ;
    private static final String DB_API_URL = "https://mc69.go.yj.fr/db-request.php?" ;

    private String table;
    public Dao(String table) {
        this.table = table;
    }

    protected void query(String named_query, String id, OnSuccess onSuccess) {
        String url = DB_API_URL+"named="+named_query+"&id="+id;
        //Log.i(TAG, "query: "+url);
        new Http2AsyncTask(onSuccess).execute(url);
    }

    public interface OnSuccess{
        void result(List<?> items, String message);
    }
    public void list(OnSuccess onSuccess){
        String url = DB_API_URL+"list="+table;
        new Http2AsyncTask(onSuccess).execute(url);
    }
   /* public void list(String tables, String whereClause, OnSuccess onSuccess){
        String url = DB_API_URL+"list="+tables+"&"+whereClause;
        Log.i(TAG, "list: "+url);
        new Http2AsyncTask(onSuccess).execute(url);
    }*/

    public void find(String whereClause, OnSuccess onSuccess){
        String url = DB_API_URL+"list="+table+"&"+whereClause;
        //Log.i(TAG, "find: "+url);
        new Http2AsyncTask(onSuccess).execute(url);
    }

    public void add(String addClause, OnSuccess onSuccess){
        String url = DB_API_URL+"list="+table+"&"+addClause;
        Log.i(TAG, "add: "+url);
        new Http2AsyncTask(onSuccess).execute(url);
    }
    public void update(String updateClause, OnSuccess onSuccess){
        String url = DB_API_URL+"list="+table+"&"+updateClause;
        Log.i(TAG, url);
        new Http2AsyncTask(onSuccess).execute(url);
    }
    public void delete(String code, OnSuccess onSuccess){
        String url = DB_API_URL+"list="+table+"&action=delete&id="+code;
        Log.i(TAG, url);
        new Http2AsyncTask(onSuccess).execute(url);
    }

    /**
     * Async Task
     */
    class Http2AsyncTask extends AsyncTask<String, Void, List<T>> {
        private final OkHttpClient client = new OkHttpClient();

        private OnSuccess onSuccess;
        public Http2AsyncTask(OnSuccess onSuccess) {
           this.onSuccess = onSuccess;
        }

        private List<T> dbRequest(String url) throws IOException {
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            String json = response.body().string();

/*            Log.i(TAG, "==========================================");
            Log.i(TAG, "dbRequest: "+json);
            Log.i(TAG, "==========================================");*/

            List<T> list = new ArrayList<T>();
            try {
                list = new Gson().fromJson(json, new TypeToken<List<?>>() {}.getType());
            }catch (Exception e){}

            return list;
        }
        @Override
        protected List<T> doInBackground(String... params) {
            String url = params[0];
            try {
                return dbRequest(url);
            } catch (IOException e) {
                Log.i(TAG, "error : "+e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<T> result) {
            this.onSuccess.result(result,"");
            //Log.i(TAG, "onPostExecute: "+result);
        }
    }
    public List<T> Deserialize(List<?> data, Class<?> type) {
        Gson gs = new Gson();
        T item;
        List<T> items = new ArrayList<T>();
        if(data!=null)
            for (Object json_object : data) {
                String js = gs.toJson(json_object);
                item = (T) gs.fromJson(js, type);
                items.add(item);
            }
        return items;
    }
}
