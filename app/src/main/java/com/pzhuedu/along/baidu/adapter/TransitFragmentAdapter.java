package com.pzhuedu.along.baidu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.pzhuedu.along.baidu.R;
import com.pzhuedu.along.baidu.util.DistanceHelper;
import com.pzhuedu.along.baidu.view.LLTransit;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by along on 2018/1/2.
 */

public class TransitFragmentAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<TransitRouteLine> TransitRouteLines;

    public TransitFragmentAdapter(Context context, List arrayList) {
        super();
        mContext = context;
        TransitRouteLines = arrayList;
    }
     class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_transitfragment_category)
        TextView tvTransitfragmentCategory;
        @BindView(R.id.tv_transitfragment_time)
        TextView tvTransitfragmentTime;
        @BindView(R.id.tv_transitfragment_dis)
        TextView tvTransitfragmentDis;
        @BindView(R.id.ll_transitfragment_container)
        LinearLayout llTransitfragmentContainer;
        @BindView(R.id.tv_transitfragment_infrom)
        TextView tvTransitfragmentInfrom;
        @BindView(R.id.ll_transitfragment_rootview)
        LinearLayout llTransitfragmentRootview;
        @BindView(R.id.ll_line1)
        LinearLayout llLine1;
        @BindView(R.id.ll_line2)
        LinearLayout llLine2;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_fragment_transit, null);
        return new TransitFragmentAdapter.MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder newholder = (MyViewHolder)holder;
        TransitRouteLine line = TransitRouteLines.get(position);
        if(position==0){
            newholder.tvTransitfragmentCategory.setText("推荐方案");
            newholder.tvTransitfragmentCategory.setTextColor(mContext.getResources().getColor(R.color.blue));
        }
        else{
            newholder.tvTransitfragmentCategory.setText("方案 "+position);
            newholder.tvTransitfragmentCategory.setTextColor(mContext.getResources().getColor(R.color.red));
        }
        int totalWalkDis = 0;
        int totalPassStation = 0;
        boolean isfirstTransit = true;
        String firstTranist = "";
        String firstWalk = "";
        int subwaynum = 0;
        int busnum = 0;
        newholder.tvTransitfragmentTime.setText(DistanceHelper.timeFormatter(line.getDuration()/60));
        if(line.getAllStep() != null)
        for (int i = 0; i < line.getAllStep().size(); i++) {
            TransitRouteLine.TransitStep step = line.getAllStep().get(i);
            Log.d("lilong------------->", "getInstructions:  "+step.getInstructions());
            switch(step.getStepType()){
                case SUBWAY:
                    if(subwaynum+busnum<3)
                    newholder.llLine1.addView(new LLTransit(mContext,subwaynum++%2==0?R.drawable.subway_2:
                            R.drawable.subway_3,step.getVehicleInfo().getTitle(),!isfirstTransit));
                    else
                        newholder.llLine2.addView(new LLTransit(mContext,subwaynum++%2==0?R.drawable.subway_2:
                                R.drawable.subway_3,step.getVehicleInfo().getTitle(),!isfirstTransit));
                    totalPassStation += step.getVehicleInfo().getPassStationNum();
                    if(TextUtils.isEmpty(firstTranist)){
                        if(step.getInstructions().substring(0,2).equals("乘坐")){
                            int index = step.getInstructions().indexOf(",");
                            if(index>0)
                                firstTranist = " 首先乘坐 "+step.getInstructions().substring(2,index);
                        }
                    }
                    if(isfirstTransit) isfirstTransit = false;
                    break;
                case BUSLINE:
                    if(subwaynum+busnum<3)
                    newholder.llLine1.addView(new LLTransit(mContext,busnum++%2==0?R.drawable.bus
                            :R.drawable.bus_3,step.getVehicleInfo().getTitle(),!TextUtils.isEmpty(firstTranist)));
                    else
                        newholder.llLine2.addView(new LLTransit(mContext,busnum++%2==0?R.drawable.bus
                                :R.drawable.bus_3,step.getVehicleInfo().getTitle(),!TextUtils.isEmpty(firstTranist)));
                    totalPassStation += step.getVehicleInfo().getPassStationNum();
                    if(TextUtils.isEmpty(firstTranist)){
                        if(step.getInstructions().substring(0,2).equals("乘坐")){
                            int index = step.getInstructions().indexOf(",");
                            if(index>0)
                                firstTranist = " 首先乘坐 "+step.getInstructions().substring(2,index);
                        }
                    }
                    if(isfirstTransit) isfirstTransit = false;

                    break;
                case WAKLING:
                    totalWalkDis += step.getDistance();
                    if(TextUtils.isEmpty(firstTranist)){
                        int index = step.getInstructions().indexOf("到达")+2;
                        if(index>-1){
                            firstWalk = "  "+step.getInstructions().substring(index)+"上车";
                            Log.d("lilong-------->","firstTransit： "+firstTranist);
                        }
                    }
                    break;
            }
        }
        newholder.tvTransitfragmentDis.setText("步行"+DistanceHelper.distanceFormatter(totalWalkDis));
        newholder.tvTransitfragmentInfrom.setText(totalPassStation+"站  " + (TextUtils.isEmpty(firstWalk)?firstTranist:firstWalk));
    }

    @Override
    public int getItemCount() {
        return  TransitRouteLines.size();
    }

}
