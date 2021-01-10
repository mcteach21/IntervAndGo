package mc.apps.demo0;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import mc.apps.demo0.dao.Dao;
import mc.apps.demo0.dao.InterventionDao;
import mc.apps.demo0.model.Intervention;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("mc.apps.demo0", appContext.getPackageName());
    }

    @Test
    public void interventionDaoTest(){
        new InterventionDao().list((data, message) -> {
            Log.i("tests", "interventionDaoTest: "+data);
            //items = Dao.stringToArray(data.toString(), Intervention[].class);

        });
    }
}