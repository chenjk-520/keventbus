

## 一、前言
### eventbus3.0的优缺点分析：
  ### eventbus基于观察者模式，用很少的代码，帮我们实现很多功能，使我们的代码变得很优雅。但是大面积的使用eventbus之后，会发现一些很严重的问题

  - 需要创建很多的类来区分订阅方法，订阅的方法越多，类越多，代码越多，apk的体积越大
  - 以类来贯穿这个事件传递的方式，广播方式发送问题后，排查事件源或订阅方法的难度比较大
  - 需要写很多的订阅方法，订阅方法满天飞，很容易发生dex方法数超出
  - 发送的广播无法收到返回值
  - 不知道private的订阅方法，不知道eventbus为什么要这么设计
  - 明明不用发送数据，只是通知，也需要传递一个类
  - 无法取消单独的某一个订阅的方法


  **围绕这个上面几个问题，我在eventbus的基础上进行了改善。**

---

## 二、改善说明： 基于android eventbus 3.0 版本优化：

 -  优化前期以类为区分监听接收对象，需要创建很多类，优化后采用字符串方式作为事件event-key区分代替以类的方式，避免创建太多类，减少代码量，比较优雅，本思想也参考ios通知的实现。

- 支持返回值，支持private和static的方法

-  优化前，订阅者需要订阅很多监听方法来区分分类，优化后可以减少监听方法，在一个方法或多个方法里面进行分类判断即可，思想来自handler

-  优化前维护难，表现在不清楚事件发生源，优化后，可以统一管理事件vent key，方便管理查找bug
 
-  支持只发送事件的功能

-  支持冷冻事件方法，和解除冷冻事件的方法


  **其他方面如Subscribe订阅方法，线程模式的，粘性事件等的处理均和eventbus3.0的处理一致。**

  **项目使用：** 
      ***androidstudio gradle 引入：implementation 'com.github.chenjk-520:keventbus:1.1'
      项目开源地址：[github地址](http://github.com/chenjk-520/keventbus)
，如果觉得好用就给个Star或给个打赏吧。如果需要改进的地方，清在下面评论或发送私信到我的邮件13544216361@163.com***
  

---

## 三、使用说明
  和eventbus3.0版本的使用方式差不多，是eventbus3.0改进，下面看看怎么使用
   - 了解使用之前先了解一下其中一个关键的类EventMessage
   ```
        private T msg;
        private String event;
        private int arg;
        private CallBack callBack;
   ```
  msg是我们需要发送的数据，可以是任何数据
  event是我们一个事件对应的event，通过这个event我们可以找到订阅的方法。
  arg，是event的在一次分类
  callBack 是处理发送广播后的回调。

  #### 1.注册事件与取消注册事件
    注册事件：
    
      KeventBus.getDefault().register(this);
    
    取消注册事件：
     
      KeventBus.getDefault().unregister(this);
    

  #### 2. 只使用event-key,不发送数据

  发送事件：
  
     KeventBus.getDefault().post(TestKey.SAY_HELLO);
   
  接收事件：

    @Subscribe(event = TestKey.SAY_HELLO,threadMode = ThreadMode.MAIN)
    public void hello(EventMessage<String> msg){
        Log.i("TAG",msg.getEvent());
    }
    其中post事件TestKey.SAY_HELLO，与@Subscribe的event = TestKey.SAY_HELLO一一对应才可以接收到


  #### 3. 使用event-key,并发送数据
  
  发送事件：

     KeventBus.getDefault().post(new EventMessage<>("say hello!",TestKey.SAY_HELLO));

这里的构造函数参数分别是msg，event

  接收数据：

      @Subscribe(event = TestKey.SAY_HELLO,threadMode = ThreadMode.MAIN)
      public void hello(EventMessage<String> msg){
          Log.i("TAG",msg.getMsg());
      }

#### 4. 使用event-key,并发送数据, 在同一订阅方法里面再区分，有点类似handler的message，event相当于是what属性，arg也跟message的arg一样
  
  发送事件1：

         KeventBus.getDefault().post(new EventMessage("data==1",TestKey.SAY_HELLO,1));

  发送事件2：

         KeventBus.getDefault().post(new EventMessage("data==2",TestKey.SAY_HELLO,2));

   这里的构造函数参数分别是msg，event，arg

   接收事件：
         
          @Subscribe(event = TestKey.SAY_HELLO,threadMode = ThreadMode.MAIN)
          public void hello34(EventMessage<String> msg){
            if (msg.getArg() == 1){
                Toast.makeText(this,msg.getMsg(),Toast.LENGTH_SHORT).show();
            }else if(msg.getArg() == 2 ){
                Toast.makeText(this,msg.getMsg(),Toast.LENGTH_SHORT).show();
            }
         }

#### 5. 支持发送订阅消息后回调
    
  发送订阅消息：

            KeventBus.getDefault().post(new EventMessage<String>("测试data",TestKey.SAY_HELLO, new EventMessage.CallBack<String>() {
                            @Override
                            public void onCall(String result, int arg) {
                                //在这里处理回调数据
                                Log.i("TAG",result);
                            }
                        }));

 接收事件：

            @Subscribe(event = TestKey.SAY_HELLO4,threadMode = ThreadMode.ASYNC,enback = true)
            private String hello5(EventMessage msg){
                Log.i("TAG",msg.getMsg());
                return "测试返回值";
            }                       
      
 注意：
    1.接收事件需要加上enback = true
    2.这里的回调之后的是接收事件的线程，如果在主线程里面发出的，在子线程接收并返回的事件，回调的方法是接收方法的线程，如果需要toast的话，需要自己切换到主线程再处理


#### 5. 支持冷冻事件和激活事件

 说明： 冷冻事件后，发送相对应的事件，订阅的方法将不能接收到这个时间，但这个订阅的方法并没有真的移除掉，可以通过重新激活这个方法，下次发送便可以再接收到

 冷冻方法：
         
          KeventBus.getDefault().freezeSubscribeEvent(TestKey.SAY_HELLO);

激活方法: 

         KeventBus.getDefault().activeEvent(TestKey.SAY_HELLO);  

        
