package com.kx.kotlin.reflex;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import com.kx.kotlin.R;
import com.kx.kotlin.ui.MainActivity;

/**
 * Created by admin on 2019/10/22.
 */
public class DemoA extends Activity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        Instrumentation instrumentation = (Instrumentation) Reflex.getFieldObject(Activity.class,this,"mInstrumentation");
        MyInstrumentation instrumentation1 = new MyInstrumentation(instrumentation);
        Reflex.setFieldObject(Activity.class,this,"mInstrumentation",instrumentation1);


        findViewById(R.id.hook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DemoA.this,ReflexActivity.class));
            }
        });
    }
}
