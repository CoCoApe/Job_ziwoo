package cn.com.zhiwoo.view.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.consult.ConsultChatActivity;
import cn.com.zhiwoo.activity.home.MessageCenterActivity;
import cn.com.zhiwoo.bean.home.Message;
import cn.com.zhiwoo.bean.tutor.Tour;
import cn.com.zhiwoo.bean.tutor.User;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.ChatTool;
import cn.com.zhiwoo.utils.LogUtils;
import io.rong.imlib.model.UserInfo;

/**
 * 功能描述：标题按钮上的弹窗（继承自PopupWindow）
 */
public class MessageTipPopup extends PopupWindow {
	private Context mContext;

	// 列表弹窗的间隔
	protected final int LIST_PADDING = 10;

	// 实例化一个矩形
	private Rect mRect = new Rect();

	// 坐标的位置（x、y）
	private final int[] mLocation = new int[2];

	// 屏幕的宽度和高度
	private int mScreenWidth;

	// 判断是否需要添加或更新列表子类项
	private boolean mIsDirty;

	// 定义列表对象
	public ListView mListView;

	// 定义弹窗子类项列表
	private ArrayList<Message> mTipMessages = new ArrayList<>();
	private static MessageTipPopup instance = null;

	public static MessageTipPopup newInstance(Context context){
		if (null == instance){
			instance = new MessageTipPopup(context, ChatTool.sharedTool().getTipMessages());
		}
		return instance;
	}

	public static void showMessageTipPopup(Context context,View view) {
		if (AccountTool.isLogined(context)){
			MessageTipPopup.newInstance(context).show(view);
		}
	}
	private MessageTipPopup(Context context,ArrayList<Message> tipMessages) {
		// 设置布局的参数
		this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,tipMessages);
	}

	private MessageTipPopup(Context context, int width, int height,ArrayList<Message> tipMessages) {
		this.mContext = context;
		this.mTipMessages = tipMessages;
		// 设置可以获得焦点
		setFocusable(true);
		// 设置弹窗内可点击
		setTouchable(true);
		// 设置弹窗外可点击
		setOutsideTouchable(true);

		// 获得屏幕的宽度和高度

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
//		mScreenWidth = wm.getDefaultDisplay().getWidth();

		// 设置弹窗的宽度和高度
		setWidth(width);
		setHeight(height);

//		setBackgroundDrawable(new BitmapDrawable());

		// 设置弹窗的布局界面
		setContentView(LayoutInflater.from(mContext).inflate(
				R.layout.main_messagetip_view, null));
		initUI();
	}

	/**
	 * 初始化弹窗列表
	 */
	private void initUI() {
		mListView = (ListView) getContentView().findViewById(R.id.title_list);

		mListView.setOnItemClickListener(new OnItemClickListener());
	}

	/**
	 * 显示弹窗列表界面
	 */
	public void show(View view) {
		populateActions();
		// 获得点击屏幕的位置坐标
		view.getLocationOnScreen(mLocation);
		// 设置矩形的大小
		mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(),
				mLocation[1] + view.getHeight());
		// 显示弹窗的位置DensityUtil.dip2px(mContext,120)
		int x = (int) (0.8*mScreenWidth);
		int y = mRect.bottom;
		int popupGravity = Gravity.NO_GRAVITY;
		showAtLocation(view, popupGravity, x, y);

		//开始监听消息改变的通知
		IntentFilter filter = new IntentFilter();
		filter.addAction("message_change");
		MyBroadCastRecevier broadcastReceiver = new MyBroadCastRecevier();
		mContext.registerReceiver(broadcastReceiver, filter);

	}

	/**
	 * 设置弹窗列表子项
	 */
	private void populateActions() {
		// 设置列表的适配器
		mListView.setAdapter(new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = View.inflate(mContext,R.layout.main_messagetip_view_item,null);
				}
				final ViewHolder viewHolder = getViewHolder(convertView);

				if (mTipMessages == null) {//没有未读消息
					viewHolder.imageView.setVisibility(View.GONE);
					viewHolder.titleTextView.setVisibility(View.GONE);
					viewHolder.unreadCountTextView.setVisibility(View.GONE);
					viewHolder.moreTextView.setVisibility(View.GONE);
					viewHolder.noUnreadTextView.setVisibility(View.VISIBLE);
				} else if (mTipMessages.size() == 0) {
					viewHolder.imageView.setVisibility(View.GONE);
					viewHolder.titleTextView.setVisibility(View.GONE);
					viewHolder.unreadCountTextView.setVisibility(View.GONE);
					viewHolder.moreTextView.setVisibility(View.GONE);
					viewHolder.noUnreadTextView.setVisibility(View.VISIBLE);
				} else if (mTipMessages.size() < 5) {// 未读消息
					viewHolder.imageView.setVisibility(View.VISIBLE);
					viewHolder.titleTextView.setVisibility(View.VISIBLE);
					viewHolder.unreadCountTextView.setVisibility(View.VISIBLE);
					viewHolder.moreTextView.setVisibility(View.GONE);
					viewHolder.noUnreadTextView.setVisibility(View.GONE);
					final Message tipMessage = mTipMessages.get(position);

					if (tipMessage.getUserIcon().length() == 0 || tipMessage.getUserName().length() == 0) {
						ChatTool.sharedTool().getUserInfo(tipMessage.getUserId(), new ChatTool.loadUserInfoCallBack() {
							@Override
							public void onsuccess(UserInfo userInfo) {
								tipMessage.setUserIcon(userInfo.getPortraitUri().toString());
								tipMessage.setUserName(userInfo.getName());
								viewHolder.titleTextView.setText(tipMessage.getUserName());
								Glide.with(mContext)
										.load(tipMessage.getUserIcon())
										.into(viewHolder.imageView);
							}
						});
					} else  {
						viewHolder.titleTextView.setText(tipMessage.getUserName());
						Glide.with(mContext)
								.load(tipMessage.getUserIcon())
								.into(viewHolder.imageView);
					}

					if (tipMessage.getUnreadCount() > 99) {
						viewHolder.unreadCountTextView.setText("99+");
					} else {
						viewHolder.unreadCountTextView.setText(""+tipMessage.getUnreadCount());
					}
				} else {// 未读消息 + 更多
					if (position < 4) {
						viewHolder.imageView.setVisibility(View.VISIBLE);
						viewHolder.titleTextView.setVisibility(View.VISIBLE);
						viewHolder.unreadCountTextView.setVisibility(View.VISIBLE);
						viewHolder.moreTextView.setVisibility(View.GONE);
						viewHolder.noUnreadTextView.setVisibility(View.GONE);
						final Message tipMessage = mTipMessages.get(position);
						if (tipMessage.getUserIcon().length() == 0 || tipMessage.getUserName().length() == 0) {
							ChatTool.sharedTool().getUserInfo(tipMessage.getUserId(), new ChatTool.loadUserInfoCallBack() {
								@Override
								public void onsuccess(UserInfo userInfo) {
									tipMessage.setUserIcon(userInfo.getPortraitUri().toString());
									tipMessage.setUserName(userInfo.getName());
									viewHolder.titleTextView.setText(tipMessage.getUserName());
									Glide.with(mContext)
											.load(tipMessage.getUserIcon())
											.into(viewHolder.imageView);
								}
							});
						} else  {
							viewHolder.titleTextView.setText(tipMessage.getUserName());
							Glide.with(mContext)
									.load(tipMessage.getUserIcon())
									.into(viewHolder.imageView);
						}

						if (tipMessage.getUnreadCount() > 99) {
							viewHolder.unreadCountTextView.setText("99+");
						} else {
							viewHolder.unreadCountTextView.setText(""+tipMessage.getUnreadCount());
						}
					} else {
						viewHolder.imageView.setVisibility(View.GONE);
						viewHolder.titleTextView.setVisibility(View.GONE);
						viewHolder.unreadCountTextView.setVisibility(View.GONE);
						viewHolder.moreTextView.setVisibility(View.VISIBLE);
						viewHolder.noUnreadTextView.setVisibility(View.GONE);
					}
				}
				return convertView;
			}
			private ViewHolder getViewHolder(View view) {
				ViewHolder viewHolder = (ViewHolder) view.getTag();
				if (viewHolder == null) {
					viewHolder = new ViewHolder(view);
					view.setTag(viewHolder);
				}
				return viewHolder;
			}
			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				if (mTipMessages == null) {
					return "没有未读消息";
				} else {
				return mTipMessages.get(position);
				}
			}

			@Override
			public int getCount() {
				if (mTipMessages == null) {
					return 1;
				} else if (mTipMessages.size() == 0){
					return 1;
				} else if (mTipMessages.size() < 5) {
					return mTipMessages.size();
				} else {
					return 5;
				}
			}
		});
	}

	private class ViewHolder {

		TextView titleTextView;
		TextView unreadCountTextView;
		ImageView imageView;
		TextView moreTextView;
		TextView noUnreadTextView;

		ViewHolder(View view) {
			imageView = (ImageView) view.findViewById(R.id.icon_imageveiw);
			titleTextView = (TextView) view.findViewById(R.id.txt_title);
			unreadCountTextView = (TextView) view.findViewById(R.id.unreadcount_textview);
			moreTextView = (TextView) view.findViewById(R.id.more_textview);
			noUnreadTextView = (TextView) view.findViewById(R.id.no_unread_textview);
		}
	}
	private class OnItemClickListener implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (mTipMessages == null || mTipMessages.size() == 0) {//没有未读消息
			} else {
				if (mTipMessages.size() < 5) {// 未读消息
                    Message message = mTipMessages.get(position);
                    Intent intent = new Intent(mContext, ConsultChatActivity.class);
                    Bundle bundle = new Bundle();

                    User user = new Tour();
                    user.setId(message.getUserId());
                    user.setNickName(message.getUserName());
                    user.setHeadImageUrl(message.getUserIcon());
                    //[测试id]
    //				user.setId("1652");
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                } else {// 未读消息 + 更多
                    if (position < 4) {
                        Message message = mTipMessages.get(position);
                        Intent intent = new Intent(mContext, ConsultChatActivity.class);
                        Bundle bundle = new Bundle();

                        User user = new Tour();
                        user.setId(message.getUserId());
                        user.setNickName(message.getUserName());
                        user.setHeadImageUrl(message.getUserIcon());
                        //[测试id]
    //					user.setId("1652");
                        bundle.putSerializable("user", user);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, MessageCenterActivity.class);
                        mContext.startActivity(intent);
                    }
                }
			}
			dismiss();
		}
	}
	private class MyBroadCastRecevier extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtils.log("收到了新消息,要刷新提示数据");
			BaseAdapter adapter = (BaseAdapter) mListView.getAdapter();
			mTipMessages = ChatTool.sharedTool().getTipMessages();
			adapter.notifyDataSetChanged();
		}
	}
}