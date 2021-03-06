package banhang.smartbill.Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import banhang.smartbill.Activity.MainActivity;
import banhang.smartbill.Adapter.ProductAdapter;
import banhang.smartbill.DAL.OrdersAPI;
import banhang.smartbill.DAL.ProductAPI;
import banhang.smartbill.DAL.TokenAPI;
import banhang.smartbill.Entity.Order;
import banhang.smartbill.Entity.Product;
import banhang.smartbill.Entity.UnauthorizedAccessException;
import banhang.smartbill.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductFragment extends Fragment {
    List<Product> arrProduct,temp;
    ProductAdapter adapter;
    ListView lvProduct = null;
    SearchView svSearch;
    View mView;
    Runnable runnableUI;
    ProductAPI product;
    FloatingActionButton fbDetail;
    Handler handlerPost = new Handler();
    public ProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.product_fragment, container, false);
        initVariable();
        getAndShowProducts();

        // Inflate the layout for this fragment
        return mView;

    }

    public void initVariable(){
        lvProduct = (ListView)mView.findViewById(R.id.lv_product);
        svSearch = (SearchView)mView.findViewById(R.id.sv_search);
        svSearch.setQueryHint("Search");
        fbDetail = (FloatingActionButton)mView.findViewById(R.id.fb_to_detail);
        checkOrderCreated();
        fbDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderDetailFragment fragment = new OrderDetailFragment();
                ((MainActivity)getActivity()).showFragment(fragment);
            }
        });
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText.toString());
                return true;
            }
        });


        arrProduct = new ArrayList<Product>();



    }
    private void getAndShowProducts(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what){
                    case 1:
                        arrProduct.clear();
                        arrProduct.addAll((ArrayList<Product>)msg.obj);
                        adapter = new ProductAdapter(getActivity(),R.layout.product_listview_custom, arrProduct);
                        lvProduct.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        break;
                    case 2 : //error unauthorize
                        Toast.makeText(getContext(),R.string.unauthorize,Toast.LENGTH_LONG).show();
                        MainActivity.requireLogin(getContext());
                        break;

                }
            }
        };

        Thread getProductThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    ProductAPI api = new ProductAPI();
                    List<Product> products = api.getProducts();
                    Message message = handler.obtainMessage(1,products);
                    handler.sendMessage(message);
                }catch(UnauthorizedAccessException ex){
                    Message message = handler.obtainMessage(2,"Unauthorize");
                    handler.sendMessage(message);
                }
            }
        });
        getProductThread.start();
    }
    public void checkOrderCreated(){
        if(MainActivity.CurrentOrder == null){
            Toast.makeText(getActivity(),"Hóa đơn chưa được tạo",Toast.LENGTH_LONG).show();
            OrderFragment fragment = new OrderFragment();
            ((MainActivity)getActivity()).showFragment(fragment);
        }

    }

}
