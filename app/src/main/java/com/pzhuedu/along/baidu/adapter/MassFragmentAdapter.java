package com.pzhuedu.along.baidu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.pzhuedu.along.baidu.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by along on 2017/12/29.
 */

public class MassFragmentAdapter extends RecyclerView.Adapter implements View.OnClickListener {


    private Context mContext;
    private List<MassTransitRouteLine> massTransitRouteLines;

    public MassFragmentAdapter(Context context, List arrayList) {
        super();
        mContext = context;
        massTransitRouteLines = arrayList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_mass_category)
        TextView tvItemMassCategory;
        @BindView(R.id.tv_item_mass_time)
        TextView tvItemMassTime;
        @BindView(R.id.tv_fragment_mass_item_prive)
        TextView tvFragmentMassItemPrive;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_fragment_mass, null);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder newholder = (MyViewHolder) holder;
        MassTransitRouteLine line = massTransitRouteLines.get(position);
        /**
         *         getArriveTime() 格式 2016-04-05T 17：06：10
         *         getPrice()   获取本线路的总票价（元）
         *         getNewSteps()  返回该线路的step信息
         */
        Log.d("lilong------------>", "line.getArriveTime(): " + line.getArriveTime()
                + "line.getPrice(): " + line.getPrice()
                + "line.getTitle(): " + line.getTitle()
                + "line.getNewSteps().size: " + line.getNewSteps().size()
//                + "line.getAllStep().size: " + line.getAllStep().size()
                + "line.getPriceInfo().size: " + line.getPriceInfo().size()
        );
        if(line.getAllStep()!=null)
        for (MassTransitRouteLine.TransitStep step : line.getAllStep()) {
            /**
             * getVehileType()
             ESTEP_BUS  公交
             ESTEP_COACH 大巴
             ESTEP_DRIVING 驾车
             ESTEP_PLANE 飞机
             ESTEP_TRAIN 火车
             ESTEP_WALK  步行
             * */
            Log.d("lilong------------>", "step.getVehileType(): " + step.getVehileType()
                    + "step.getInstructions()换乘说明: " + step.getInstructions()
            );
            switch (step.getVehileType()) {
                case ESTEP_BUS:
                    Log.d("lilong------------>", "step.getBusInfo().getStopNum经过的站台数： " + step.getBusInfo().getStopNum());
                    break;
                case ESTEP_COACH:
                    Log.d("lilong------------>", "step.getCoachInfo().getPrice()票价： " + step.getCoachInfo().getPrice());
                    break;
                case ESTEP_DRIVING:
                    Log.d("lilong------------>", "应该开车去： ");
                    break;
                case ESTEP_PLANE:
                    Log.d("lilong------------>", "step.getPlaneInfo().getPrice()票价： " + step.getPlaneInfo().getPrice());
                    break;
                case ESTEP_TRAIN:
                    Log.d("lilong------------>", "应该坐火车车去： ");
                    break;
                case ESTEP_WALK:
                    Log.d("lilong------------>", "应该走路去： ");
                    break;

            }
        }

        if(line.getNewSteps()!=null)
        for (int i = 0; i < line.getNewSteps().size(); i++) {
            List<MassTransitRouteLine.TransitStep> list = line.getNewSteps().get(i);
            for (MassTransitRouteLine.TransitStep step : list) {
                Log.d("lilong----getNewSteps", "step.getVehileType(): " + step.getVehileType()
                        + "step.getInstructions()换乘说明: " + step.getInstructions()
                );//37650  85085 81072
                switch (step.getVehileType()) {
                    case ESTEP_BUS:
                        Log.d("lilong------getNewSteps", "step.getBusInfo().getStopNum经过的站台数： " + step.getBusInfo().getStopNum());
                        break;
                    case ESTEP_COACH:
                        Log.d("lilong------getNewSteps", "step.getCoachInfo().getPrice()票价： " + step.getCoachInfo().getPrice());
                        break;
                    case ESTEP_DRIVING:
                        Log.d("lilong-----getNewSteps", "应该开车去： ");
                        break;
                    case ESTEP_PLANE:
                        Log.d("lilong-----getNewSteps", "step.getPlaneInfo().getPrice()票价： " + step.getPlaneInfo().getPrice());
                        break;
                    case ESTEP_TRAIN:
                        Log.d("lilong-----getNewSteps", "应该坐火车车去： ");
                        break;
                    case ESTEP_WALK:
                        Log.d("lilong----getNewSteps", "应该走路去： ");
                        break;

                }
            }
        }

    }

    @Override
    public int getItemCount() {
//        return massTransitRouteLines.size();
        return 1;
    }

    @Override
    public void onClick(View v) {
        //每一条线路的详情信息 每一条路劲的详细步骤
    }
}
