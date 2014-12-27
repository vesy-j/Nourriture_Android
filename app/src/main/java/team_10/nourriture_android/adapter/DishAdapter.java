package team_10.nourriture_android.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import team_10.nourriture_android.R;
import team_10.nourriture_android.activity.DishDetailActivity;
import team_10.nourriture_android.bean.DishBean;
import team_10.nourriture_android.utils.AsynImageLoader;

/**
 * Created by ping on 2014/12/21.
 */
public class DishAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private boolean isUpdate = false;

    public List<DishBean> mDishList = new ArrayList<DishBean>();

    public DishAdapter(Context context,  List<DishBean> dishList) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mDishList = dishList;
    }

    public DishAdapter(Context context, boolean update) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        isUpdate = update;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DishViewHolder dvh = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_dish, null);
            dvh = new DishViewHolder();
            dvh.dish_item_rl = (LinearLayout)convertView.findViewById(R.id.dish_item_ll);
            dvh.name = (TextView)convertView.findViewById(R.id.dish_name_tv);
            dvh.description = (TextView)convertView.findViewById(R.id.dish_description_tv);
            dvh.time = (TextView)convertView.findViewById(R.id.dish_time_tv);
            dvh.picture = (ImageView)convertView.findViewById(R.id.dish_picture_img);
            convertView.setTag(dvh);
        } else {
            dvh = (DishViewHolder) convertView.getTag();
        }

//        DishBean obj = mDishList.get(position);
        final DishBean dishBean = (DishBean) mDishList.get(position);
        dvh.name.setText(dishBean.getName());
        dvh.description.setText(dishBean.getDescription());

        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = df.format(dishBean.getDate());
        dvh.time.setText(strDate);

        AsynImageLoader asynImageLoader = new AsynImageLoader();
        if(dishBean.getPicture()==null || "".equals(dishBean.getPicture().trim()) || "null".equals(dishBean.getPicture().trim())){
            dvh.picture.setImageResource(R.drawable.default_dish_picture);
        }else{
            asynImageLoader.showImageAsyn(dvh.picture, dishBean.getPicture(), R.drawable.default_dish_picture);
        }

        dvh.dish_item_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DishDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("dishBean", dishBean);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return mDishList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mDishList.size();
    }

    static class DishViewHolder {
        TextView name;
        TextView description;
        TextView time;
        ImageView picture;
        LinearLayout dish_item_rl;
    }
}