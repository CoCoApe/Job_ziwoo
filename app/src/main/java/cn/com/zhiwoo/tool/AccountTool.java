package cn.com.zhiwoo.tool;

import android.content.Context;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import cn.com.zhiwoo.bean.main.Account;
import cn.com.zhiwoo.utils.Global;
import cn.com.zhiwoo.utils.LogUtils;
import io.rong.imkit.RongIM;


public class AccountTool {
    private static Account currentAccount;
    public static void saveAsCurrentAccount(Context context,Account account) {
        if (account != null) {
            currentAccount = account;
            Gson gson = new Gson();
            String jsonStr = gson.toJson(account);
            try {
                FileOutputStream fos = context.openFileOutput(Global.ACCOUNT_DATA_FILE_NAME,
                        Context.MODE_PRIVATE);
                fos.write(jsonStr.getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static boolean unregistCurrentAccount(Context context) {
        File file = new File(context.getFilesDir().getAbsolutePath() + "/" + Global.ACCOUNT_DATA_FILE_NAME);
        boolean result = false;
        if (file.exists()) { // 判断文件是否存在
            result = file.delete();
        }
        if (result) {
            LogUtils.log("注销登录之后,清除当前账号和聊天工具,断开融云连接");
            //清除当前账号
            currentAccount = null;
            //销毁 聊天工具
            ChatTool.destoyChatTool();
            //断开融云连接
            try {
                RongIM.getInstance().getRongIMClient().disconnect();
            } catch (Exception ignored) {
            }

        }
        return result;
    }

    public static Account getCurrentAccount(Context context) {
        if (currentAccount == null) {
            String jsonStr = null;
            try {
                FileInputStream inStream = context.openFileInput(Global.ACCOUNT_DATA_FILE_NAME);
                byte[] buffer = new byte[1024];
                int hasRead;
                StringBuilder sb = new StringBuilder();
                while ((hasRead = inStream.read(buffer)) != -1) {
                    sb.append(new String(buffer, 0, hasRead));
                }
                inStream.close();
                jsonStr = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (jsonStr != null) {
                Gson gson = new Gson();
                currentAccount = gson.fromJson(jsonStr,Account.class);
                LogUtils.log("currentAccount : " + currentAccount.getAccessToken());
            }
        }
        return currentAccount;
    }
    public static boolean isLogined(Context context) {
        Account account = getCurrentAccount(context);
        return account != null && account.getMobile() != null;
    }

    /**
     * 更新当前用户的信息,并磁盘缓存
     */
    public static void updateIcon(final String url, final Context context, final UpdateResultListener updateResultListener) {
        if (url == null) return;
        HashMap<String,String> params = new HashMap<>();
        params.put("access_token",currentAccount.getAccessToken());
        params.put("user_id", currentAccount.getId());
        params.put("headimgurl", url);
        NetworkTool.PUT("http://121.201.7.33/zero/api/v1/user/update", params, new OnNetworkResponser() {
            @Override
            public void onSuccess(String result) {
                currentAccount.setHeadImageUrl(url);
                saveAsCurrentAccount(context, currentAccount);
                if (updateResultListener != null) {
                    updateResultListener.updateSuccess();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (updateResultListener != null) {
                    updateResultListener.updateFailure();
                }
            }
        });
    }
    public static void updateNickname(final String nickname, final Context context, final UpdateResultListener updateResultListener) {
        if (nickname == null) return;
        HashMap<String,String> params = new HashMap<>();
        LogUtils.log("access_token : " + currentAccount.getAccessToken() + ",userid : " + currentAccount.getId() + ",nickname : " + currentAccount.getNickName());
        params.put("access_token",currentAccount.getAccessToken());
        params.put("user_id", currentAccount.getId());
        params.put("nickname", nickname);
        NetworkTool.PUT("http://121.201.7.33/zero/api/v1/user/update", params, new OnNetworkResponser() {
            @Override
            public void onSuccess(String result) {
                currentAccount.setNickName(nickname);
                saveAsCurrentAccount(context, currentAccount);
                if (updateResultListener != null) {
                    updateResultListener.updateSuccess();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                LogUtils.log("错误信息: " + s);
                if (updateResultListener != null) {
                    updateResultListener.updateFailure();
                }
            }
        });
    }
    public static void updateSex(final int sex, final Context context, final UpdateResultListener updateResultListener) {
        HashMap<String,String> params = new HashMap<>();
        params.put("access_token",currentAccount.getAccessToken());
        params.put("user_id", currentAccount.getId());
        params.put("gender", ""+sex);
        NetworkTool.PUT("http://121.201.7.33/zero/api/v1/user/update", params, new OnNetworkResponser() {
            @Override
            public void onSuccess(String result) {
                currentAccount.setSex(sex);
                saveAsCurrentAccount(context, currentAccount);
                if (updateResultListener != null) {
                    updateResultListener.updateSuccess();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                LogUtils.log("错误信息: " + s);
                if (updateResultListener != null) {
                    updateResultListener.updateFailure();
                }
            }
        });
    }
    public static void updatePhone(final String phone, final Context context, final UpdateResultListener updateResultListener) {
        if (phone == null) return;
        HashMap<String,String> params = new HashMap<>();
        params.put("access_token",currentAccount.getAccessToken());
        params.put("user_id", currentAccount.getId());
        params.put("mobile", phone);
        NetworkTool.PUT("http://121.201.7.33/zero/api/v1/user/update", params, new OnNetworkResponser() {
            @Override
            public void onSuccess(String result) {
                currentAccount.setMobile(phone);
                saveAsCurrentAccount(context, currentAccount);
                if (updateResultListener != null) {
                    updateResultListener.updateSuccess();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (updateResultListener != null) {
                    updateResultListener.updateFailure();
                }
            }
        });
    }
    public static void updateCity(final String city, final Context context, final UpdateResultListener updateResultListener) {
        if (city == null) return;
        HashMap<String,String> params = new HashMap<>();
        params.put("access_token",currentAccount.getAccessToken());
        params.put("user_id", currentAccount.getId());
        params.put("city", city);
        NetworkTool.PUT("http://121.201.7.33/zero/api/v1/user/update", params, new OnNetworkResponser() {
            @Override
            public void onSuccess(String result) {
                currentAccount.setCity(city);
                saveAsCurrentAccount(context, currentAccount);
                if (updateResultListener != null) {
                    updateResultListener.updateSuccess();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (updateResultListener != null) {
                    updateResultListener.updateFailure();
                }
            }
        });
    }
    public static void updatePassword(String oldPassword,String newPassword,Context context, final UpdateResultListener updateResultListener) {
        if (oldPassword == null || newPassword == null) return;
        HashMap<String,String> params = new HashMap<>();
        params.put("access_token",currentAccount.getAccessToken());
        params.put("user_id",currentAccount.getId());
        params.put("password",oldPassword);
        params.put("new_password",newPassword);
        NetworkTool.PUT("http://121.201.7.33/zero/api/v1/user/update", params, new OnNetworkResponser() {
            @Override
            public void onSuccess(String result) {
                if (updateResultListener != null) {
                    updateResultListener.updateSuccess();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (updateResultListener != null) {
                    updateResultListener.updateFailure();
                }
            }
        });
    }
    public interface UpdateResultListener {
        void updateSuccess();
        void updateFailure();
    }

    public static void updateAllInfo(final String phone, final String birth,final String area,final String demand_infos,final Context context, final UpdateResultListener updateResultListener) {
        if (phone == null) return;
        final String province = area.substring(0,area.indexOf(" "));
        final String city = area.substring(area.indexOf(" "),area.length()).replace(" ","");
        HashMap<String,String> params = new HashMap<>();
        params.put("access_token",currentAccount.getAccessToken());
        params.put("user_id", currentAccount.getId());
        params.put("mobile",phone);
        params.put("birth",birth);
        params.put("province",province);
        params.put("city", city);
        params.put("demand_infos",demand_infos);
        NetworkTool.PUT("http://121.201.7.33/zero/api/v1/user/update", params, new OnNetworkResponser() {
            @Override
            public void onSuccess(String result) {
                currentAccount.setMobile(phone);
                currentAccount.setBirth(birth);
                currentAccount.setProvince(province);
                currentAccount.setCity(city);
                currentAccount.setDemand_infos(demand_infos);
                saveAsCurrentAccount(context, currentAccount);
                if (updateResultListener != null) {
                    updateResultListener.updateSuccess();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                if (updateResultListener != null) {
                    updateResultListener.updateFailure();
                }
            }
        });
    }
}
