package com.example.mysafer.fragment;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mysafer.R;
import com.example.mysafer.bean.AppInfo;
import com.example.mysafer.db.AppLockDao;
import com.example.mysafer.untils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LockedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LockedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LockedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private LinearLayout rlLoading;
    private ListView lvLocked;
    private TextView tvLocked;
    private MyAdapter myAdapter;

    private List<AppInfo> lockedInfos;

    private AppLockDao appLockDao;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tvLocked.setText("已加锁应用："+lockedInfos.size());
            if (msg.what==0){
                myAdapter = new MyAdapter();
                lvLocked.setAdapter(myAdapter);
            }else if(msg.what==1) {
                myAdapter.notifyDataSetChanged();
            }
            rlLoading.setVisibility(View.GONE);
        }
    };

    class MyAdapter extends BaseAdapter {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view;
            final AppInfo appInfo = lockedInfos.get(position);
            if(convertView!=null) {
                view = convertView;
            }else {
                view = View.inflate(getActivity(), R.layout.locked_list_item, null);
            }
            ((ImageView)view.findViewById(R.id.iv_icon)).setImageDrawable(appInfo.getIcon());
            ((TextView)view.findViewById(R.id.tv_app_name)).setText(appInfo.getApkName());
            //加锁监听
            ((ImageView)view.findViewById(R.id.iv_unlock)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appLockDao.unlock(appInfo.getApkPackageName());
                    lockedInfos.remove(appInfo);
                    //移除动画
                    TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0 ,
                            Animation.RELATIVE_TO_SELF, 0);
                    anim.setDuration(1000);
                    view.startAnimation(anim);
                    //在子线程中睡眠，保证移除动画效果执行完毕
                    new Thread(){
                        @Override
                        public void run() {
                            SystemClock.sleep(1000);
                            handler.sendEmptyMessage(1);
                        }
                    }.start();
                }
            });
            return view;
        }
        @Override
        public int getCount() {
            return lockedInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return lockedInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LockedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LockedFragment newInstance(String param1, String param2) {
        LockedFragment fragment = new LockedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LockedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locked, container, false);
        rlLoading = (LinearLayout)view.findViewById(R.id.rl_loading);
        lvLocked = (ListView)view.findViewById(R.id.lv_locked);
        tvLocked = (TextView)view.findViewById(R.id.tv_locked);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        appLockDao = new AppLockDao(getActivity());
        new Thread(){
            @Override
            public void run() {
                List<AppInfo> appInfos = AppUtils.getAppInfos(getActivity());
                lockedInfos = new ArrayList<>();
                for (AppInfo info : appInfos) {
                    if(appLockDao.isLocked(info.getApkPackageName())) {
                        lockedInfos.add(info);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
