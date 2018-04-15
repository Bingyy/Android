### 关于Adapter的浅显理解
@(Android)

看下面的代码，基本上就很好理解了。

```

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Adapter视作一种数据插座，数据不能直接供给ListView，就像电不能直接接入机器，需要经过插座转送
        // Adapter要设定好数据显示格式，正如同插座有型号设置一样，以及第一个参数是上下文，插座也要指定好用途
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,data); // 通过泛型指定数据类型
        ListView listView = (ListView) findViewById(R.id.list_view);
        // 将显示组件插上插座汲取数据
        listView.setAdapter(adapter);
    }
```

可以通过滚动屏幕的方式查看屏幕外的数据，但是这样不加优化的用法很慢很卡。也容易崩溃。

### 丰富ListView

仍然是上面的步骤，但是上面的写法，显示的单条Item是字符串，因此只用了基础的`AarraAdapter`。

为了定制化属于自己的插座，我们得实现自己的Adapter.

一方面数据准备需要定制，另一方面，数据显示也要自己定义。

#### 单条显示自定义

fruit_item.xml
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ImageView
        android:id="@+id/fruit_image"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

    <TextView
        android:id="@+id/fruit_name"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_vertical"
        android:layout_marginLeft="10dp"/>

</LinearLayout>
```

#### 数据定义

单条数据定义：

```
package me.bingw.listview;

public class Fruit {
    private String name;
    private int imageId;

    public Fruit(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }
}
```

数据插座定义：

```
package me.bingw.listview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FruitAdapter extends ArrayAdapter {
    private int resourceId;

    // 上下文，ListView子项布局，数据
    public FruitAdapter(Context context,int textViewResourceId,List<Fruit> objects) {
        super(context,textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    // 在每个子项被滚动到屏幕内时调用
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Fruit fruit = (Fruit) getItem(position); // 得到当前项的实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false); // 为子项加载传入的布局

        // 从子项布局中拿到更小一级的元素：显示组件
        ImageView fruitImage = (ImageView) view.findViewById(R.id.fruit_image);
        TextView fruitName = (TextView) view.findViewById(R.id.fruit_name);

        // 为组件添加数据内容
        fruitImage.setImageResource(fruit.getImageId());
        fruitName.setText(fruit.getName());

        return view;
    }
}

```

前台代码整合：

```
public class MainActivity extends AppCompatActivity {
    private List<Fruit> fruitList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFruits();
        // Adapter视作一种数据插座，数据不能直接供给ListView，就像电不能直接接入机器，需要经过插座转送
        // Adapter要设定好数据显示格式，正如同插座有型号设置一样，以及第一个参数是上下文，插座也要指定好用途
        FruitAdapter adapter = new FruitAdapter(MainActivity.this, R.layout.fruit_item,fruitList); // fruitList是数据

        ListView listView = (ListView) findViewById(R.id.list_view);
        // 将显示组件插上插座汲取数据
        listView.setAdapter(adapter);
    }

    private void initFruits() {
        for (int i = 0; i < 2; i ++) {
            Fruit apple= new Fruit("Apple",R.drawable.apple);
            Fruit banana = new Fruit("Banana",R.drawable.banana);
            Fruit orange = new Fruit("Orange",R.drawable.orange);

            fruitList.add(apple);
            fruitList.add(banana);
            fruitList.add(orange);

        }
    }
}
```
