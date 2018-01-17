package xyz.yutoo.ytpieview;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by YuToo on 2018/1/17.
 */

public class MainActivity extends Activity{

    private int[] colors ={0xff342831, 0xff675321, 0xff895684, 0xffac03a3, 0xff889012};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        YtPieView ypv = findViewById(R.id.ypv);

        List<YtPieView.Data> datas = new ArrayList<>();
        Random random = new Random();
        for(int i = 0 ; i < 15 ; i ++){
            datas.add(new YtPieView.Data("test_data" + i , random.nextInt(100), colors[i % 5]));
        }
        ypv.setData(datas);
    }
}
