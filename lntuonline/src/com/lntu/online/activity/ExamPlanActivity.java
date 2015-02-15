package com.lntu.online.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lntu.online.R;
import com.lntu.online.http.HttpUtil;
import com.lntu.online.http.RetryAuthListener;
import com.lntu.online.info.NetworkInfo;
import com.lntu.online.model.ClientExamPlan;
import com.lntu.online.util.JsonUtil;

public class ExamPlanActivity extends ActionBarActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_plan);
        
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        startNetwork();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void startNetwork() {
        HttpUtil.get(this, NetworkInfo.serverUrl + "examPlan/info", new RetryAuthListener(this) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    List<ClientExamPlan> ceps = JsonUtil.fromJson(responseString, new TypeToken<List<ClientExamPlan>>(){}.getType());
                    ListView lvRoot = (ListView) findViewById(R.id.exam_plan_lv_root);
                    lvRoot.setAdapter(new ListViewAdapter(getContext(), ceps));
                } catch(Exception e) {
                    String[] msgs = responseString.split("\n");
                    if (msgs[0].equals("0x01040003")) {
                        showNothingDialog();
                    } else {
                        showErrorDialog("提示", msgs[0], msgs[1]);
                    }
                }
            }

            @Override
            public void onBtnRetry() {
                startNetwork();
            }

        });
    }

    private void showNothingDialog() {
        new AlertDialog.Builder(this)
        .setTitle("提示")
        .setMessage("暂时没有考试信息，过一个月再看吧")
        .setCancelable(false)
        .setPositiveButton("确定", new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        })
        .show();
    }

    private class ListViewAdapter extends BaseAdapter {

        private List<View> itemViews;

        public ListViewAdapter(Context context, List<ClientExamPlan> ceps) {
            LayoutInflater inflater = LayoutInflater.from(context);
            Collections.sort(ceps);
            itemViews = new ArrayList<View>();
            Date nowDate = new Date();
            for (int n = 0; n < ceps.size(); n++) {
                ClientExamPlan cep = ceps.get(n);
                //布局
                View itemView = inflater.inflate(R.layout.activity_exam_plan_item, null);
                TextView tvCourse = (TextView) itemView.findViewById(R.id.exam_plan_item_tv_course);
                TextView tvTime = (TextView) itemView.findViewById(R.id.exam_plan_item_tv_time);
                TextView tvLocation = (TextView) itemView.findViewById(R.id.exam_plan_item_tv_location);
                View iconFinish = itemView.findViewById(R.id.exam_plan_item_icon_finish);
                tvCourse.setText(cep.getCourse() + "");
                tvTime.setText(cep.getTime() + "");
                tvLocation.setText(cep.getLocation() + "");
                iconFinish.setVisibility(cep.getDateTime().before(nowDate) ? View.VISIBLE : View.GONE);
                //填充布局
                itemViews.add(itemView);
            }
        }

        @Override
        public int getCount() {
            return itemViews.size();
        }

        @Override
        public Object getItem(int position) {
            return itemViews.get(position);
        }

        @Override
        public long getItemId(int position) {
            return itemViews.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return itemViews.get(position);
        }

    }

}
