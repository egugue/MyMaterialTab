package htoyama.mytab;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import htoyama.material_tab.MaterialTabHost;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialTabHost host = (MaterialTabHost) findViewById(R.id.tabhost);
        host.addTab("hoge");
        host.addTab("あいう");
        host.addTab("かきく");

        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyAdapter());
        viewPager.setOnPageChangeListener(host);

        host.setOnTabClickListener(new MaterialTabHost.OnTabClickListener() {
            @Override
            public void onTabClick(int position) {
                viewPager.setCurrentItem(position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private static class MyAdapter extends PagerAdapter {

        private List<Integer> list = new ArrayList<>();

        public MyAdapter() {
            list.add(0);
            list.add(1);
            list.add(2);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return list.get(position).toString();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = container.getContext();
            View pageView = LayoutInflater.from(context).inflate(R.layout.page_sample, null);

            TextView tv = (TextView) pageView.findViewById(R.id.page_text);
            tv.setText("いまは"+list.get(position)+"番目");
            container.addView(pageView);

            return pageView;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
