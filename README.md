
# 支持下拉刷新的ScrollView控件 #
## 使用方法 ##

* 具体流程：
 * 新建一个类继承PullRefreshScrollView，并且重写其中的getHeaderView和getScrollView方法。

 * 在oncreate中找到该控件：  
   * scrollView = (RealizeScrollView) findViewById(R.id.rel);


* 得到建造者：
  PullRefreshScrollView.Builder builder = scrollView.new Builder(scrollView);

builder.setDamp(0.3f)
                .setDuration(500)；

建造者允许连号，分别是设置阻尼系数、设置回弹时间。


- 中断刷新

builder.closeRefreshing();

- 给PullRefreshListView设置监听

 lv.setStateCallBace(State CallBack callback);

## 回调方法何时调用 ##
* 当状态为“松开刷新”时
  * void toLoosenRefresh();


* 当状态为“下拉刷新”时
  * void toRullRefresh();


* 当状态为“正在刷新”时
  * void toRefreshing();


* 当状态为“正在加载”时
  * void toLoading();


- 从拖拽高度开始计，状态从“下拉刷新”变为“松开刷新”的过程
- percent 值为0-1
- dY 在过程中有值，在过程外值为0
  * void dragToLoosen(float percent, int dY);


- 调用builder.closeRefreshing()执行
  * void stopRefresh();
