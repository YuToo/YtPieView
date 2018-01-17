# YtPieView
---
效果图如下：<br/>
![](https://github.com/YuToo/YtPieView/raw/master/YtPieView.png)  

### 使用方式
##### xml中引用：
```xml
    <xyz.yutoo.ytpieview.YtPieView
        android:id="@+id/ypv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="#394264"/>
```
##### 可自定义的属性如下
```
  <!-- 圆环半径 -->
  <attr name="radius" format="dimension" />
  <!-- 圆环宽度 -->
  <attr name="ringWidth" format="dimension"/>
  <!-- 线段长度 -->
  <attr name="lineLength" format="dimension" />
  <!-- 文字大小 -->
  <attr name="textSize" format="dimension" />
  <!-- 正方形边长 -->
  <attr name="squareSide" format="dimension" />
```

##### 设置数据
```Java
    YtPieView ypv = findViewById(R.id.ypv);

    List<YtPieView.Data> datas = new ArrayList<>();
    Random random = new Random();
    for(int i = 0 ; i < 15 ; i ++){
        datas.add(new YtPieView.Data("test_data" + i , random.nextInt(100), colors[i % 5]));
    }
    ypv.setData(datas);
```

##### 问题陈述
- 圆环数据标识可能重叠
- 控件宽度不可自定义（之前项目需要满屏的宽度，所以没弄）
- 底部说明区域文字格式不可改
- ...

**现在项目还存在诸多问题，不管是扩展还是实现方式，此项目只是简单的实现，可能后期有有时间了会重新写一个**
