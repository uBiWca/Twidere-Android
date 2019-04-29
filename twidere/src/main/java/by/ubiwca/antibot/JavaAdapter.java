package by.ubiwca.antibot;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by Пользователь on 11.04.2019.
 */

public class JavaAdapter {
    private Context context;
    private BotListIO bio;
    JavaAdapter(Context ctx){
        this.context = ctx;


    }

    /*
        boolean isBot (String id){
        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                bio = new BotListIO(context);
                Log.d("JavaAdapter", "Checking for bot id " + id);

            }
        })
    }
    */
}
