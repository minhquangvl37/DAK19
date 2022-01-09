package org.o7planning.dak19;

import android.widget.Filter;

import java.util.ArrayList;
import org.o7planning.dak19.adapter.AdapterProductAdmin;
import org.o7planning.dak19.models.ModelProduct;

public class FilterProduct extends Filter {

    private AdapterProductAdmin adapter;
    private ArrayList<ModelProduct> filterList;

    public FilterProduct(AdapterProductAdmin adapter, ArrayList<ModelProduct> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        //validate data for search
        if(constraint != null && constraint.length()>0){
            //search filed not empty
            //change to upcase, to make case insensitive
            constraint=constraint.toString().toUpperCase();
            //store our filtered list
            ArrayList<ModelProduct> filteredModels=new ArrayList<>();
            for(int i=0;i< filterList.size();i++){
                //check, search by title and category
                if(filterList.get(i).getProductTitle().toUpperCase().contains(constraint) ||
                        filterList.get(i).getProductCategory().toUpperCase().contains(constraint)){
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
        adapter.productList=(ArrayList<ModelProduct>) results.values;
        //refresh adapter
        adapter.notifyDataSetChanged();
    }
}
