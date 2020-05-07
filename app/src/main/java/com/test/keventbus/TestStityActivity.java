package com.test.keventbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.util.keventbus.EventMessage;
import com.util.keventbus.KeventBus;
import com.util.keventbus.Subscribe;
import com.util.keventbus.ThreadMode;

/**
 * 文件描述：
 * 作者：chenjingkun708
 * 创建时间：2020/5/6
 * 更改时间：2020/5/6
 */
public class TestStityActivity extends AppCompatActivity {
    TextView tv_test;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keventbus_activity_test_stity);
        tv_test = findViewById(R.id.tv_test);
        KeventBus.getDefault().register(this);
    }

    @Subscribe(event = TestKey.TEST_STITY,sticky = true,threadMode = ThreadMode.MAIN,enback = true)
    private String setText(EventMessage<String> msg){
        tv_test.setText(msg.getMsg());
        return "TestStityActivity";
    }

}
