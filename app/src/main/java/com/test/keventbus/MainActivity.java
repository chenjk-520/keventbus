package com.test.keventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.util.keventbus.EventMessage;
import com.util.keventbus.KeventBus;
import com.util.keventbus.Subscribe;
import com.util.keventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    TextView tv_bus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keventbus_activity_main);

        // Example of a call to a native method
        Button btn_say = findViewById(R.id.sample_text);
        tv_bus = findViewById(R.id.tv_bus);
//        tv.setText(stringFromJNI());
        KeventBus.getDefault().register(this);
        btn_say.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeventBus.getDefault().post(new EventMessage<>("say hello!",TestKey.SAY_HELLO));
            }
        });
    }

    public void post2(View view){
        KeventBus.getDefault().post(TestKey.SAY_HELLO2);
    }
    public void post3(View view){
        KeventBus.getDefault().post(new EventMessage("data==1",TestKey.SAY_HELLO3,1));
    }
    public void post4(View view){
        KeventBus.getDefault().post(new EventMessage("data==2",TestKey.SAY_HELLO3,2));
    }
    public void post5(View view){
        KeventBus.getDefault().post(new EventMessage<String>("测试",TestKey.SAY_HELLO4,1, new EventMessage.CallBack<String>() {
            @Override
            public void onCall(String result, int arg) {
                Log.i("testbus",",msg:"+result);
            }
        }));
    }
    public void post6(View view){
        KeventBus.getDefault().post(new EventMessage("测试post6",TestKey.SAY_HELLO5));
    }

    public void post7(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                KeventBus.getDefault().post(new EventMessage<String>("测试post7",TestKey.SAY_HELLO6, new EventMessage.CallBack<String>() {
                    @Override
                    public void onCall(String result, int arg) {
                        Log.i("testbus",result);
                    }
                }));
            }
        }).start();
    }

    public void post8(View view){
        KeventBus.getDefault().postSticky(new EventMessage<String>("testdata", TestKey.TEST_STITY, new EventMessage.CallBack<String>() {
            @Override
            public void onCall(String result, int arg) {
                Log.i("testbus",result);
            }
        }));
        startActivity(new Intent(this,TestStityActivity.class));
    }

    @Subscribe(event = TestKey.SAY_HELLO,threadMode = ThreadMode.MAIN)
    public void hello(EventMessage<String> msg){
        Toast.makeText(this,msg.getMsg(),Toast.LENGTH_SHORT).show();
        tv_bus.setText(msg.getMsg());
    }

    @Subscribe(event = TestKey.SAY_HELLO2,threadMode = ThreadMode.MAIN)
    public void hello2(EventMessage<String> msg){
        Toast.makeText(this,msg.getEvent(),Toast.LENGTH_SHORT).show();
    }
    @Subscribe(event = TestKey.SAY_HELLO3,threadMode = ThreadMode.MAIN)
    public void hello34(EventMessage<String> msg){
        if (msg.getArg()==1){
            Toast.makeText(this,msg.getMsg()+",arg==1",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,msg.getMsg()+",arg:"+msg.getArg(),Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(event = TestKey.SAY_HELLO4,threadMode = ThreadMode.ASYNC,enback = true)
    private String hello5(EventMessage message){
        return "测试返回值";
    }

    @Subscribe(event = TestKey.SAY_HELLO5,threadMode = ThreadMode.BACKGROUND,enback = true)
    private void hello6(EventMessage message){
        Log.i("testbus",message.getEvent()+",msg:"+message.getMsg());
    }

    @Subscribe(event = TestKey.SAY_HELLO6,threadMode = ThreadMode.MAIN_ORDERED,enback = true)
    private String hello7(EventMessage message){
        Log.i("testbus",message.getEvent()+",msg:"+message.getMsg());
        return "回调hello7";
    }

    @Override
    protected void onPause() {
        super.onPause();
        KeventBus.getDefault().freezeSubscribeEvent(TestKey.SAY_HELLO2);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        KeventBus.getDefault().activeEvent(TestKey.SAY_HELLO2);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();
}
