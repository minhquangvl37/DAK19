package org.o7planning.dak19;

import android.widget.Filter;

import org.o7planning.dak19.adapter.AdapterOrderAdmin;
import org.o7planning.dak19.adapter.AdapterOrderSeller;
import org.o7planning.dak19.models.ModelOrderAdmin;
import org.o7planning.dak19.models.ModelOrderSeller;

import java.util.ArrayList;

public class FilterOrderSeller extends Filter {
    private AdapterOrderSeller adapter;
    private ArrayList<ModelOrderSeller> filterList;

    public FilterOrderSeller(AdapterOrderSeller adapter, ArrayList<ModelOrderSeller> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected Filter.FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        //validate data for search
        if(constraint != null && constraint.length()>0){
            //search filed not empty
            //change to upcase, to make case insensitive
            constraint=constraint.toString().toUpperCase();
            //store our filtered list
            ArrayList<ModelOrderSeller> filteredModels=new ArrayList<>();
            for(int i=0;i< filterList.size();i++){
                //check, search by title and category
                if(filterList.get(i).getOrderStatus().toUpperCase().contains(constraint)){
                    //add  filtered data to list
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count=filteredModels.size();
            results.values=filteredModels;
        }
        else {
            //search filed empty
            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.orderSellerList=(ArrayList<ModelOrderSeller>) results.values;
        //refresh adapter
        adapter.notifyDataSetChanged();
    }
}
