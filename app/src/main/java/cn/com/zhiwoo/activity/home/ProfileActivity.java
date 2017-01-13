package cn.com.zhiwoo.activity.home;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.lidroid.xutils.BitmapUtils;

import java.io.File;
import java.io.FileNotFoundException;

import cn.com.zhiwoo.R;
import cn.com.zhiwoo.activity.base.BaseActivity;
import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.tool.AccountTool;
import cn.com.zhiwoo.tool.UploadTool;
import cn.com.zhiwoo.utils.DensityUtil;
import cn.com.zhiwoo.utils.LogUtils;

public class ProfileActivity extends BaseActivity {

//    private final static int ICON_CROP_CODE = 998;
    private final static int ICON_CROP_CODE = 8;

//    private final static int ICON_CAMERA_CODE = 999;
    private final static int ICON_CAMERA_CODE = 16;

//    private final static int ICON_DISK_CODE = 1000;
    private final static int ICON_DISK_CODE = 24;

//    private final static int NICKNAME_CODE = 1001;
    private final static int NICKNAME_CODE = 32;

//    private final static int PHONE_CODE = 1002;
    private final static int PHONE_CODE = 40;

//    private final static int PASSWORD_CODE = 1003;
    private final static int PASSWORD_CODE = 48;

//    private final static int REQUEST_CODE_ASK_CAMERA = 1004;
    private final static int REQUEST_CODE_ASK_CAMERA = 56;

    private ImageView iconImageView;
    private TextView nickNameTextView;
    private TextView sexTextView;
    public TextView phoneTextView;
    private TextView cityTextView;
    private Uri uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
    @Override
    public void initView() {
        super.initView();
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.home_profile_activity, null);
        flContent.addView(linearLayout);

        linearLayout.findViewById(R.id.icon_rl).setOnClickListener(this);
        iconImageView = (ImageView) linearLayout.findViewById(R.id.icon_imageview);

        linearLayout.findViewById(R.id.nickname_rl).setOnClickListener(this);
        nickNameTextView = (TextView) linearLayout.findViewById(R.id.nickname_textview);

        linearLayout.findViewById(R.id.sex_rl).setOnClickListener(this);
        sexTextView = (TextView) linearLayout.findViewById(R.id.sex_textview);

        linearLayout.findViewById(R.id.phone_rl).setOnClickListener(this);
        phoneTextView = (TextView) linearLayout.findViewById(R.id.phone_textview);

        linearLayout.findViewById(R.id.city_rl).setOnClickListener(this);
        cityTextView = (TextView) linearLayout.findViewById(R.id.city_textview);

        linearLayout.findViewById(R.id.password_rl).setOnClickListener(this);
    }

    @Override
    public void initData() {
        titleView.setText("个人信息");

        Account account = AccountTool.getCurrentAccount(this);
        BitmapUtils bitmapUtils = new BitmapUtils(this);
        bitmapUtils.display(iconImageView, account.getHeadImageUrl());
        nickNameTextView.setText(account.getNickName());
        String sex = "保密";
        if (account.getSex() == 1) {
            sex = "男";
        } else if (account.getSex() == 2) {
            sex = "女";
        }
        sexTextView.setText(sex);

        if (!TextUtils.isEmpty(account.getMobile())) {
            phoneTextView.setText(account.getMobile());
        }
        if (!TextUtils.isEmpty(account.getCity())) {
            cityTextView.setText(account.getCity());
        }
        File cacheDir = getCacheDir();//文件所在目录为getFilesDir();
        String cachePath = cacheDir.getPath();
    }

    @Override
    public void initListener() {

    }

    @Override
    public void process(View v) {
        switch (v.getId()) {
            case R.id.icon_rl: {
                final ActionSheetDialog actionSheetDialog = new ActionSheetDialog(this, new String[]{"拍照", "相册"}, null);
                actionSheetDialog.title("更换头像?")
                        .titleTextColor(Color.RED)
                        .show();
                actionSheetDialog.setOnOperItemClickL(new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            LogUtils.log("拍照");
                            Intent i = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            i.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
                            if (Build.VERSION.SDK_INT >= 23) {
                                if(ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                                    ActivityCompat.requestPermissions(ProfileActivity.this,new String[]{android.Manifest.permission.CAMERA},REQUEST_CODE_ASK_CAMERA);
                                    actionSheetDialog.dismiss();
                                    return;
                                }else{
                                    startActivityForResult(i, ICON_CAMERA_CODE);
                                }
                            } else {
                                startActivityForResult(i, ICON_CAMERA_CODE);
                            }
                        } else {
                            LogUtils.log("相册");
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");//相片类型
                            startActivityForResult(intent, ICON_DISK_CODE);
                        }
                        actionSheetDialog.dismiss();
                    }
                });
            }
            break;
            case R.id.nickname_rl: {
                Intent intent = new Intent(this, ModifyInfoActivity.class);
                intent.putExtra("name", "昵称");
                intent.putExtra("defaultValue", AccountTool.getCurrentAccount(getBaseContext()).getNickName());
                startActivityForResult(intent, NICKNAME_CODE);
            }
            break;
            case R.id.sex_rl: {
                final ActionSheetDialog actionSheetDialog = new ActionSheetDialog(this, new String[]{"保密", "男", "女"}, null);
                actionSheetDialog.title("请选择性别信息")
                        .titleTextColor(Color.RED)
                        .show();
                actionSheetDialog.setOnOperItemClickL(new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                        AccountTool.updateSex(position, getBaseContext(), new AccountTool.UpdateResultListener() {
                            @Override
                            public void updateSuccess() {
                                String sex = "保密";
                                LogUtils.log(" 性别->position: " + position);
                                if (position == 1) {
                                    sex = "男";
                                } else if (position == 2) {
                                    sex = "女";
                                }
                                sexTextView.setText(sex);
                            }

                            @Override
                            public void updateFailure() {
                                LogUtils.log("性别更新失败");
                            }
                        });
                        actionSheetDialog.dismiss();
                    }
                });
            }
            break;
            case R.id.phone_rl: {
                Intent intent = new Intent(this, PhoneBindActivity.class);
                startActivityForResult(intent, PHONE_CODE);
            }
            break;
            case R.id.city_rl: {
                ProvincesPickerDialog provincesPickerDialog = new ProvincesPickerDialog(this, new ProvincesPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(final String provinceStr) {
                        AccountTool.updateCity(provinceStr, getBaseContext(), new AccountTool.UpdateResultListener() {
                            @Override
                            public void updateSuccess() {
                                LogUtils.log("城市更新成功, 城市 : " + provinceStr);
                                cityTextView.setText(provinceStr);
                            }

                            @Override
                            public void updateFailure() {
                                LogUtils.log("城市更新失败");
                            }
                        });
                    }
                }, "选择城市", "广东 广州");
                provincesPickerDialog.show();
            }
            break;
            case R.id.password_rl: {
                Intent intent = new Intent(this, PasswordModifyActivity.class);
                startActivityForResult(intent, PASSWORD_CODE);
            }
            break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LogUtils.log("授权回调。。。");
        switch (requestCode) {
            case REQUEST_CODE_ASK_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    i.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
                    startActivityForResult(i,ICON_CAMERA_CODE);
                } else {
                    Toast.makeText(getBaseContext(), "你不让我访问你的相机，我也很无奈！", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.log("requestCode = " + requestCode + ", resultCode = " + resultCode);
        if (data == null && uritempFile == null) {
            return;
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ICON_CROP_CODE: {
                    Bitmap photo = null;
                    try {
                        photo = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile)),160,160);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getBaseContext(),"更改头像失败",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    if (photo != null) {
                        final Bitmap finalPhoto = photo;
                        UploadTool.sharedTool().uploadImage(photo, AccountTool.getCurrentAccount(getBaseContext()).getAccessToken(), new UploadTool.OnUploadResultListener() {
                            @Override
                            public void uploadSuccess(String url) {
                                AccountTool.updateIcon(url, getBaseContext(), new AccountTool.UpdateResultListener() {
                                    @Override
                                    public void updateSuccess() {
                                        LogUtils.log("头像更新成功: 线程 = " + Thread.currentThread());
                                        iconImageView.setImageBitmap(finalPhoto);
                                        Intent intent = new Intent("userinfo_change");
                                        sendBroadcast(intent);
                                        Toast.makeText(getBaseContext(), "头像更新成功", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void updateFailure() {
                                        Toast.makeText(getBaseContext(), "头像更新失败", Toast.LENGTH_SHORT).show();
                                        LogUtils.log("头像更新失败");
                                    }
                                });
                            }

                            @Override
                            public void uploadFailure() {
                                Toast.makeText(getBaseContext(), "头像更新失败", Toast.LENGTH_SHORT).show();
                                LogUtils.log("头像上传失败");
                            }
                        });
                    } else {
                        Toast.makeText(getBaseContext(), "头像修改失败", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
                case ICON_CAMERA_CODE: {
//                    Bundle bundle = data.getExtras();
//                    Bitmap bitmap = (Bitmap) bundle.get("data");
//                    Uri uri = data.getData();
//                    if (uri == null) {
//                        uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
//                    }
//                    LogUtils.log("iamge uri = " + uri);
//                    cropImage(uri);
                    LogUtils.log("从相机拍好照片放在了 uri：" + uritempFile.toString());
                    cropImage(uritempFile);
                }
                break;
                case ICON_DISK_CODE: {
                    Uri uri = data != null ? data.getData() : null;
                    cropImage(uri);
                }
                break;

                case NICKNAME_CODE: {
                    final String value = data != null ? data.getStringExtra("value") : null;
                    AccountTool.updateNickname(value, getBaseContext(), new AccountTool.UpdateResultListener() {
                        @Override
                        public void updateSuccess() {
                            nickNameTextView.setText(value);
                            Intent intent = new Intent("userinfo_change");
                            sendBroadcast(intent);
                            Toast.makeText(getBaseContext(), "昵称更新成功", Toast.LENGTH_SHORT).show();
                            LogUtils.log("昵称更新成功");
                        }

                        @Override
                        public void updateFailure() {
                            Toast.makeText(getBaseContext(), "昵称更新失败", Toast.LENGTH_SHORT).show();
                            LogUtils.log("昵称更新失败");
                        }
                    });

                }
                break;
                case PHONE_CODE: {
                    final String phone = data != null ? data.getStringExtra("phone") : null;
                    AccountTool.updatePhone(phone, getBaseContext(), new AccountTool.UpdateResultListener() {
                        @Override
                        public void updateSuccess() {
                            phoneTextView.setText(phone);
                            Toast.makeText(getBaseContext(), "手机更新成功", Toast.LENGTH_SHORT).show();
                            LogUtils.log("手机更新成功");
                        }

                        @Override
                        public void updateFailure() {
                            Toast.makeText(getBaseContext(), "手机更新失败", Toast.LENGTH_SHORT).show();
                            LogUtils.log("手机更新失败");
                        }
                    });
                }
                break;
                case PASSWORD_CODE: {
                    final String newPassword = data != null ? data.getStringExtra("newPassword") : null;
                    final String oldPassword = data != null ? data.getStringExtra("oldPassword") : null;
                    AccountTool.updatePassword(oldPassword, newPassword, getBaseContext(), new AccountTool.UpdateResultListener() {
                        @Override
                        public void updateSuccess() {
                            Toast.makeText(getBaseContext(), "密码更新成功", Toast.LENGTH_SHORT).show();
                            LogUtils.log("密码更新成功");
                        }

                        @Override
                        public void updateFailure() {
                            LogUtils.log("密码更新失败");
                            Toast.makeText(getBaseContext(), "密码更新失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //截取图片
    private void cropImage(Uri uri) {
        //裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        //裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", DensityUtil.dip2px(getBaseContext(), 750));
        intent.putExtra("outputY", DensityUtil.dip2px(getBaseContext(), 750));
        //图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("scaleUpIfNeeded", true);//图片过小的话，会拉伸
//        intent.putExtra("return-data", true);
        //uritempFile为Uri类变量，实例化uritempFile
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        startActivityForResult(intent, ICON_CROP_CODE);
        overridePendingTransition(0, 0);
    }
}
