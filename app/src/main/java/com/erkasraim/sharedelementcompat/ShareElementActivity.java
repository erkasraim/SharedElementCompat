package com.erkasraim.sharedelementcompat;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by erkas on 14. 11. 4..
 */
public class ShareElementActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share_element);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.id_fragment_container, new FirstFragment(), null)
                .commit();
    }
}
