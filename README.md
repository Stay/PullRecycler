今天要跟大家安利一种封装方式，保证只用**100行代码**就能撸一个列表页面。

来上图：
![recycler01.png](http://upload-images.jianshu.io/upload_images/625299-67680ca7dd18b241.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![recycler02.png](http://upload-images.jianshu.io/upload_images/625299-4e9cd1b51244625f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

相关说明：

1. 该列表页可以是Activity，也可以是Fragment
2. 该列表页使用RecyclerView，所以支持列表，网格，瀑布流
3. 该列表页支持下拉刷新，自动加载更多
4. 该列表需extends BaseListActivity或BaseListFragment

之前Stay写过一篇[RecyclerView再封装](http://www.jianshu.com/p/a5dd9c0735f2)，本篇是对该篇的详细解释。

本文难度适中，没有过多的算法，纯粹是利用Android提供的API与一些设计模式相结合做的封装。

没什么好解释的了，老司机要开车啦，滴滴。。

首先来看看我们这个列表SampleListActivity, 它是继承BaseListActivity

		public class SampleListActivity extends BaseListActivity<String> 

注意，此处有泛型。这里的T就是你用在列表数据List<T>。为什么要这样写，**先埋个坑，一会再填**。

在SampleListActivity中

		@Override
    protected BaseViewHolder getViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_sample_list_item, parent, false);
        return new SampleViewHolder(view);
    }
    
    @Override
    protected ILayoutManager getLayoutManager() {
        return new MyGridLayoutManager(getApplicationContext(), 3);
    }
    
    class SampleViewHolder extends BaseViewHolder {
    ｝
  
子类SampleListActivity只需要做以上实现

1. 指定列表类型：linear, grid, stagger
2. 指定item对应的ViewHolder
3. ViewHolder的数据绑定

ok，只要完成这些事情，一个妹子列表就出来了。开撸吧。

好奇的你不会就此满足。Adapter哪去了？RecyclerView呢？我怎么没看到它们。嗯哼～

BaseListActivity有个儿子叫SampleListActivity, 儿子非常努力，凭借自己的天赋找到了组织，终于，3秒后，儿子拿着请求来的数据加上老爹给的框架撸上了妹纸。注：妹纸图来自[gank.io](http://gank.io/api)

就是这样一个情况，除了请求数据以及制定每个Item的UI样式，其他的都由父类完成。

是不是好奇BaseListActivity中封装了些什么？我们进去看看吧。

![recycler03.png](http://upload-images.jianshu.io/upload_images/625299-46af0aabc52044d4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

其实SampleListActivity还有个爷爷，不过这不重要。我们看，在父类中定义了ArrayList<T>, Adapter, Recycler. 并且对Recycler, Adapter做了初始化，为什么要这样写？老司机带你看一下系统源码就知道了。

		android.app.ListActivity
		
![recycler04.png](http://upload-images.jianshu.io/upload_images/625299-f0d66cca0ecb340c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这里实际上参考了系统ListActivity的初衷，将ListView(这里是RecyclerView)封装起来，并且定义一个默认layout，当列表页非常简单时，子类只需要绑定data就可以完成UI显示。

Stay这里做的BaseListActivity要更内聚一些。因为我们将List<T>定义在父类，所以在Adapter的getItemCount中可以直接做返回。不需要子类明确指定size。至于其它父类定义不了的，比如onCreateViewHolder, onBindViewHolder，可以让子类实现。

还有父类预定义了列表样式，默认为LinearLayoutManager，如果子类不想要，直接重写方法就可以了。

其实这个父类代码也不多，90行。父类有父类的想法，为了儿子能自己独立成长，只能提供一些最基础的框架，至于儿子以后能干嘛，那是儿子的事情。

虽然父类做的事情不多，但是能给的都给了，它做的最正确的事，就是早年还做了一套封装，它叫PullRecycler。这个PullRecycler还挺给力的，可以下拉刷新，自动加载更多，支持三种LayoutManager。

其实这个PullRecycler没有多难做，也就是一个SwipeRefreshLayout+RecyclerView。当然难点还是有的。下拉刷新是SwipeRefreshLayout实现的，但是自动加载更多有三个坑。

1. 判断是否需要加载更多，是通过onScrollListener来做的，你需要拿屏幕中最后一个显示的item posistion去跟totalCount比对。但是在StaggerLayoutManager中，拿到的是一个数组。其它LayoutManager拿到的是int。这就坑了，不统一，很多github上的RecyclerView封装都是通过instanceOf来强转的，我不太喜欢。所以我就定义了一个接口ILayoutManager，让每个LayoutManager去实现一个统一的findLastVisiblePosition() ![recycler05.png](http://upload-images.jianshu.io/upload_images/625299-93ecb8fcaf661ae4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
2. 加载更多footer需要自成一行，但在grid和stagger模式下，这个就比较麻烦了。GridLayoutManager还简单一点，直接看源码的类注释你就能知道如何做。SpanSizeLookup，如果为footer，那就返回1，代表占满整个宽度。![recycler06.png](http://upload-images.jianshu.io/upload_images/625299-3dfaa54db2614c2d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240) StaggerGridLayoutManager更麻烦，你得改itemView的LayoutParams，将setFullSpan设为true，才能自成一行。![recycler07.png](http://upload-images.jianshu.io/upload_images/625299-608ab8cb2bc6fd68.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)![recycler08.png](http://upload-images.jianshu.io/upload_images/625299-016516fbcfde7a42.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
3. 以前ListView可以添加footer，但是RecyclerView没有，你得自己在adapter中做判断，如果有footer，那itemCount要＋1。所以我又将Adapter抽出来，做成BaseListAdapter，是否显示footer，判断是否是stagger模式下footer，给一个默认footerViewHolder，如果子类不满意还能再重写。![recycler09.png](http://upload-images.jianshu.io/upload_images/625299-4a416ad7cd71e36f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

啊啊啊，开车好累。老司机得歇会。

直接可以运行，另外还封装了BaseSectionListActivity, 带section header的sample。

本次封装收录在[快速搭建项目(Material Design重构版)](http://www.stay4it.com/course/7)课程中，如果想知道它是如何一步步封装出来的，不妨跟着课程系统学习一下。