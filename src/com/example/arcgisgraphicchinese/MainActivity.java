package com.example.arcgisgraphicchinese;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {
	MapView mMapView;
	GraphicsLayer popGraphicLayer;
	Button btnClear;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);   
		// After the content of this Activity is set, the map can be accessed programmatically from the layout.
		mMapView = (MapView) findViewById(R.id.map);
		btnClear=(Button)findViewById(R.id.btn_clear);
		popGraphicLayer=new GraphicsLayer();
		mMapView.addLayer(popGraphicLayer);
		mMapView.setOnSingleTapListener(mapViewSingleTapListener());
		btnClear.setOnClickListener(clearListener());
	}

	/**
	 * 清除图层上的所有弹出框
	 * @return
	 */
	private View.OnClickListener clearListener()
	{
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popGraphicLayer.removeAll();
			}
		};
	}
	
	/**
	 * 地图单击事件监听函数
	 * @return
	 */
	@SuppressWarnings("serial")
	private OnSingleTapListener mapViewSingleTapListener()
	{
		return new OnSingleTapListener() {
			@Override
			public void onSingleTap(float x, float y) {
				Point point=mMapView.toMapPoint(x, y);
				View view=getGraphicView(point);
				Drawable drawable=getViewDrawable(view);
				PictureMarkerSymbol symbol=new PictureMarkerSymbol(drawable);
				symbol.setOffsetY(40);
				Graphic graphic=new Graphic(point, symbol);
				popGraphicLayer.addGraphic(graphic);
			}
		};
	}
	
	/**
	 * 将View转为Drawable
	 * @param view
	 * @return
	 */
	private Drawable getViewDrawable(View view)
	{
		Bitmap bitmap=convertViewToBitmap(view);
		Drawable drawable=new BitmapDrawable(null, bitmap);
		return drawable;
	}

	/**
	 * 获取要在地图上绘制的View
	 * @param point VIew中显示Point的信息
	 * @return
	 */
	@SuppressLint("InflateParams")
	private View getGraphicView(Point point)
	{
		RelativeLayout view=(RelativeLayout)getLayoutInflater().inflate(R.layout.pop_graphic_view,null);
		DisplayMetrics metrics=getResources().getDisplayMetrics();
		int density=(int)metrics.density;
		//之所以在这儿设置LayoutParams参数，是因为在如果不这样设置，在之后调用view.measure时在某些Android版本上会报空指针异常
		//在4.1.2回报空指针，4.4.2不会报，具体是设备原因还是Android版本原因没有详细测试，条件有限
		RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,80*density);
		layoutParams.setMargins(0, 0, 0, 0);
		view.setLayoutParams(layoutParams);
		//这里要说明一点，textView的singleLine属性值不能设置为true，
		//否则textView上所有的文字会叠加在一起，成为一个密密麻麻的点
		//不信你可以试试
		TextView textView=(TextView) view.findViewById(R.id.tv_content);
		textView.setText("您点击的坐标详细信息：\nX："+point.getX()+"\nY："+point.getY());
		return view;
	}

	/**
	 * 将View转换为BitMap
	 * @param view
	 * @return
	 */
	public static Bitmap convertViewToBitmap(View view){
		view.destroyDrawingCache();
		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		return view.getDrawingCache(true);
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		// Call MapView.pause to suspend map rendering while the activity is paused, which can save battery usage.
		if (mMapView != null)
		{
			mMapView.pause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Call MapView.unpause to resume map rendering when the activity returns to the foreground.
		if (mMapView != null)
		{
			mMapView.unpause();
		}
	}
}
