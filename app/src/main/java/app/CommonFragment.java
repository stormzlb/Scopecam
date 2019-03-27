package app;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

/**
 * Created by zlb on 2017/9/29.
 */

public class CommonFragment extends Fragment {

    private FragmentManager fgmr;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        fgmr = getActivity().getSupportFragmentManager();
        super.onCreate(savedInstanceState);
    }


    public <T extends View> T findView(int id, View root_view) {
        T view = (T) root_view.findViewById(id);
        return view;
    }

    public void replaceFragment(Fragment fragClass) {
        FragmentTransaction transaction = fgmr.beginTransaction();
        //transaction.replace(R.id.fra_container, fragClass).addToBackStack(null).commit();
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
