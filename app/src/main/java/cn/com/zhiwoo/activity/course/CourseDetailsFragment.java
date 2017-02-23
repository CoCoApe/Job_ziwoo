package cn.com.zhiwoo.activity.course;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.com.zhiwoo.R;

/**
 * Created by 25820 on 2017/2/15.
 */

public class CourseDetailsFragment extends Fragment {

    private TextView title_tv;
    private TextView content_tv;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_details_frg,container,false);
        title_tv = (TextView) view.findViewById(R.id.course_details_title);
        content_tv = (TextView) view.findViewById(R.id.course_details_content);
        return view;
    }

}
