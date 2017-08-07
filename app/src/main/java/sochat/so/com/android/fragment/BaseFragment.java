package sochat.so.com.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

/**
 * Fragment基类
 * 
 * @author GALE
 * 
 */
public abstract class BaseFragment extends Fragment {
	
	public Activity mActivity;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity=activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Log.i("Gale log", " onCreate()");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		//Log.i("Gale log", " onCreateView()");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		//Log.i("Gale log", " onViewCreated()");
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//Log.i("Gale log", " onActivityCreated()");
	}

	@Override
	public void onStart() {
		super.onStart();
		//Log.i("Gale log", " onStart()");
	}

	@Override
	public void onResume() {
		super.onResume();
		//Log.i("Gale log", " onResume()");
	}

	@Override
	public void onPause() {
		super.onPause();
			//Log.i("Gale log", " onPause()");
	}

	@Override
	public void onStop() {
		super.onStop();
		//Log.i("Gale log", " onStop()");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		//Log.i("Gale log", " onDestroyView()");
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//Log.i("Gale log", " onDestroy()");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		//Log.i("Gale log", " onDetach()");
	}
	@Override
	public void onActivityResult(int i, int j, Intent intent){
		super.onActivityResult(i, j, intent);
		//Log.i("Gale log", " onActivityResult()");
	}
	/**
	 * fragment name
	 */
	public abstract String getFragmentName();
	
	
}
