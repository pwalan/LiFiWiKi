package lab414.bupt.com.lifiwiki.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lab414.bupt.com.lifiwiki.R;

/**
 * Created by school-miao on 2016-03-17.
 */
public class Wodequanzi extends Fragment {

    private String[] names = new String[]{
            "name1", "name3", "name5"
    };

    private String[] times=new String[]{
            "2011","2013","2015"
    };

    private int[] pic = new int[]{
            R.drawable.pic1, R.drawable.pic1, R.drawable.pic1,
    };

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wodequanzi, null);

        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("name", names[i]);
            listItem.put("time",times[i]);
            listItem.put("pic",pic[i]);
            listItems.add(listItem);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), listItems, R.layout.quanzi_item,
                new String[]{"name","time","pic"},
                new int[]{R.id.name,R.id.time,R.id.picture});

        ListView list = (ListView) view.findViewById(R.id.wodequanzi_list);
        list.setAdapter(simpleAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "You clicked " + names[position], Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

}