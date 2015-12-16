package com.example.viewpager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.viewpagerindicator.AutoScrollViewPager;
import com.viewpagerindicator.CirclePageIndicator;

public class MainActivity extends Activity {
	private AutoScrollViewPager viewPager;
	private List<View> views = new ArrayList<View>();
	private CirclePageIndicator indicatorView;
	private MyAdapter myAdapter = null;

	private RelativeLayout relviewpager;
	private ArrayList<String> listData = new ArrayList<String>() {
		{
			add("http://wcp.aixinxi.net/viewpager/v1.jpg");
			add("http://wcp.aixinxi.net/viewpager/v2.jpg");
//			add("http://wcp.aixinxi.net/viewpager/v3.jpg");
//			add("http://wcp.aixinxi.net/viewpager/v4.jpg");
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		relviewpager = (RelativeLayout) findViewById(R.id.relviewpager);
		showCarouselFigure();// 显示轮播图片
	}

	/**
	 * 显示轮播图片
	 */
	public void showCarouselFigure() {
		viewPager = (AutoScrollViewPager) findViewById(R.id.viewPager);
		myAdapter = new MyAdapter();
		myAdapter.setViewData(getApplicationContext());
		viewPager.setAdapter(myAdapter);

		indicatorView = (CirclePageIndicator) findViewById(R.id.indicator);

		indicatorView.setFillColor(Color.WHITE);
		indicatorView.setPageColor(Color.parseColor("#7D7D7D"));
		indicatorView.setStrokeColor(Color.parseColor("#00000000")); // 透明

		indicatorView.setViewPager(viewPager);
		if (listData.size() == 1) {
			indicatorView.setCurrentItem(0); // 不轮播
		} else {
			indicatorView.setCurrentItem(1);//
		}
		indicatorView.setOnPageChangeListener(new OnPageChangeListener() {
			private int currentIndex = 1;

			@Override
			public void onPageSelected(int arg0) {
				currentIndex = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int status) {
				if (status == 0) {
					if (currentIndex == 0) {

						indicatorView.setCurrentItem(viewPager.getAdapter().getCount() - 2);
					} else if (currentIndex == viewPager.getAdapter().getCount() - 1) {
						indicatorView.setCurrentItem(1);
					}
				}
			}
		});

		myAdapter.notifyDataSetChanged();

		if (views.size() > 0) {
			if (views.size() == 1) {
				indicatorView.setIsLoopViewPager(false);
				indicatorView.setVisibility(View.GONE);

			} else {
				indicatorView.setIsLoopViewPager(true);
				indicatorView.setVisibility(View.VISIBLE);

				viewPager.setInterval(1000 * 3);
				viewPager.startAutoScroll();
			}
		}

	}

	@Override
	public void onPause() {
		super.onPause();
		if (views.size() > 1) {
			viewPager.stopAutoScroll();
		}
	}

	@Override
	public void onResume() {

		if (views != null && views.size() > 0) {

			if (views.size() == 1) {
				if (indicatorView != null) {
					indicatorView.setIsLoopViewPager(false);
					indicatorView.setVisibility(View.GONE);
				}

			} else {
				if (indicatorView != null && viewPager != null) {
					indicatorView.setIsLoopViewPager(true);
					indicatorView.setVisibility(View.VISIBLE);

					viewPager.setInterval(1000 * 3);
					viewPager.startAutoScroll();
				}

			}
		}

		super.onResume();

	}

	/**
	 * 轮播图 adapter
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyAdapter extends PagerAdapter {

		public void setViewData(Context context) {
			LayoutInflater inflater = LayoutInflater.from(context);
			views.clear();

			//----------
			if (listData.size() == 2) {
				View view = inflater.inflate(R.layout.main, null);
				views.add(view);
				final ImageView coverimgView = (ImageView) view.findViewById(R.id.coverimg);

				// 必须要加载图片 不然会出现空白
				new MyTask2().execute(new Object[] { listData.get(listData.size() - 1), coverimgView });

			}
           //--------------
			
			for (int i = 0; i < listData.size(); i++) {
				final int h = i;
				View view = inflater.inflate(R.layout.main, null);
				views.add(view);
				final ImageView coverimgView = (ImageView) view.findViewById(R.id.coverimg);
				coverimgView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ToastUtils.showToast(getApplicationContext(), "点击了我_" + h);

					}
				});
				new MyTask2().execute(new Object[] { listData.get(i), coverimgView });

			}

			if (listData.size() == 2) {
				View view = inflater.inflate(R.layout.main, null);
				views.add(view);
				final ImageView coverimgView = (ImageView) view.findViewById(R.id.coverimg);
				// 必须要加载图片 不然会出现空白
				new MyTask2().execute(new Object[] { listData.get(0), coverimgView });
			}

		}

		@Override
		public int getCount() {

			if (listData.size() == 1) {
				return 1;
			}

			else {
				return listData.size() + 2;
			}
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			try {
				((ViewPager) container).addView(views.get(getPosition(position)));
			} catch (Exception e) {
			}
			return views.get(getPosition(position));

		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			if (position > 1 && position < views.size()) {
				((ViewPager) container).removeView(views.get(getPosition(position)));
			}
		}

		private int getPosition(int position) {

			if (listData.size() == 2) {
				return position; // viewpager 数量为2时 索引的bug
			} else {
				int count = views.size();
				if (position == 0) {
					return count - 1;
				} else if (position == count + 1) {
					return 0;
				} else {
					return position - 1;
				}
			}

		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}

	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	public class MyTask2 extends AsyncTask<Object, Integer, Void> {

		@Override
		protected Void doInBackground(Object... params) {
			InputStream inputStream;
			try {
				inputStream = HttpUtils.getImageViewInputStream(String.valueOf(params[0]));
				Bitmap drawable = BitmapFactory.decodeStream(inputStream);

				Message msg = Message.obtain();
				msg.what = 1;
				msg.obj = new Object[] { params[1], drawable };
				mHandler.sendMessage(msg);

			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				Object[] obj = (Object[]) msg.obj;
				ImageView img = (ImageView) obj[0];
				Bitmap bmp = (Bitmap) obj[1];
				// 640
				// *440

				int w = getScreenWidth(getApplicationContext());
				int h = getScreenHeight(getApplicationContext());

				double imgHeight = ArithUtil.div(ArithUtil.mul(bmp.getHeight(), w), bmp.getWidth());

				Log.i("xx", "w1=" + bmp.getWidth() + ",h=" + bmp.getHeight() + ",imgHeight=" + imgHeight);
				// (y *w) /x =h

				RelativeLayout.LayoutParams params = (LayoutParams) img.getLayoutParams();
				params.width = w;
				params.height = (int) imgHeight;

				relviewpager.setLayoutParams(params);
				img.setLayoutParams(params);

				img.setImageBitmap(bmp);

			}
		}

	};

}