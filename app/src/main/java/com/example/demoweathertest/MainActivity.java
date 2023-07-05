//package com.example.demoweathertest;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.gson.Gson;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutionException;
//
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//
//// "https://restapi.amap.com/v3/weather/weatherInfo?key=" + key + "&extensions=all&out=json&city=" + cityCode;
//// https://restapi.amap.com/v3/weather/weatherInfo?key=a3592b05a660ff8b6008a83d500636cb&extensions=all&out=json&city=100010
//public class MainActivity extends AppCompatActivity {
//
//    private static final String API_KEY = "a3592b05a660ff8b6008a83d500636cb";
//    private static final String BASE_URL = "https://restapi.amap.com/v3/weather/weatherInfo?";
//    private OkHttpClient okHttpClient = new OkHttpClient();   // ok
//    private List<String>    weatherList = new ArrayList<>();   // 天气数据集合
//    ArrayAdapter simpleAdapter;
//    private EditText cityIdEditText;
//    private Button searchButton;
//    private Button starButton;
//    private Button refreshButton;
//    private ListView listView;
//    private SharedPreferences cacheShrPre;   // 数据缓存
//    private SharedPreferences.Editor cacheEditor;
//    private SharedPreferences        starShaPre;    // 城市缓存
//    private SharedPreferences.Editor starEditor;
//
//    private TextView reportTime;
//    private Gson gson = new Gson();
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        //1.初始化Button按钮和响应事件
//        initButton();
//
//        //2.缓存当前查询城市
//        cacheShrPre = getSharedPreferences("WeatherData", MODE_PRIVATE);
//        cacheEditor = cacheShrPre.edit();
//        // 启动时清空缓存
//        cacheEditor.clear();
//        cacheEditor.apply();
//
//        //3.对城市进行收藏 Star：收藏的城市
//        starShaPre = getSharedPreferences("StarCity", Context.MODE_PRIVATE);
//        starEditor = starShaPre.edit();
//        String starCities = starShaPre.getString("city", "");
//        // 如果存在缓存数据的话，获取并显示（这里利用 HashSet 去重）
//        if (!starCities.isEmpty()) {
//            weatherList.add(0, ("已收藏的城市列表"));
//            weatherList.addAll(gson.fromJson(starCities, List.class));
//            weatherList = new ArrayList<>(new HashSet<>(weatherList));
//        }
//
//        //4.点击搜索按钮的响应事件：（1）发送GET请求，获取JSON代码(2)解析JSON代码，切割数据，设置到listview（3）恢复是否为关注按钮
//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String cityId = cityIdEditText.getText().toString().trim();
//                if (!cityId.isEmpty()) {
//                    //输入不为空，发送请求
//                    //使用异步任务，防止主程序被阻塞
//                    AsyncTask<String, Void, String> execute = new WeatherTask().execute(cityId);
//                    try {
//                        //获取到获取JSON，保存到s中
//                        String s = execute.get();
//                        Toast.makeText(MainActivity.this, "查询成功!", Toast.LENGTH_SHORT).show();
//                        //===============================Listview显示==================================
//                        if (s != null) {
//                            // 解析JSON数据
//                            String forecastURL = "https://restapi.amap.com/v3/weather/weatherInfo?key=" + API_KEY + "&extensions=all&out=json";
//                            String newUrl = forecastURL + "&city=" + cityId;
//                            final Request requestForecast = new Request.Builder().url(newUrl).get().build();
//                            Response responseForecast = okHttpClient.newCall(requestForecast).execute();
//                            if (responseForecast.isSuccessful()) {
//                                //================================================================================================
//                                String resultForecast = responseForecast.body().string();
//                                List<Map<String, String>> weatherList = new ArrayList<>();  // 存储解析json字符串得到的天气信息
//                                Weather weather = gson.fromJson(resultForecast, Weather.class);     // Gson解析
//                                // 定义日期数组
//                                String[] dates = {"今天", "明天", "后天", "大后天"};
//                                // 切换至主线程设置时间（UI）
//                                runOnUiThread(() -> reportTime.setText("数据最后更新时间：" + weather.getForecasts().get(0).getReporttime()));
//
//                                // forecastList 是天气预报信息集合，每天的天气信息是一个对象，所以需要遍历集合，获取每天的天气信息
//                                List<Casts> forecastList = weather.getForecasts().get(0).getCasts();
//                                for (int i = 0; i < forecastList.size() && i < dates.length; i++) {
//                                    Casts cast = forecastList.get(i);
//                                    Map<String, String> map = new HashMap<>();
//
//                                    map.put("date", dates[i]);  // 日期
//
//                                    map.put("day_weather", cast.getDayweather());     // 白天天气
//                                    map.put("day_temp", cast.getDaytemp() + "℃");     // 温度
//                                    map.put("day_wind", cast.getDaywind() + "风");    // 风向
//                                    map.put("day_power", cast.getDaypower() + "级");  // 风力
//
//                                    map.put("night_weather", cast.getNightweather());    // 夜间天气
//                                    map.put("night_temp", cast.getNighttemp() + "℃");    // 温度
//                                    map.put("night_wind", cast.getNightwind() + "风");   // 风向
//                                    map.put("night_power", cast.getNightpower() + "级"); // 风力
//                                    weatherList.add(map);
//                                }
//                            }
////                            String weatherInfo =null ;
//                            // 在主进程的TextView中显示天气信息（更新时间）
////                            TextView textView = findViewById(R.id.report_Time);
////                            textView.setText(weatherInfo);
//                            // 将 weatherList 写入缓存
//                            cacheEditor.putString(cityId, gson.toJson(weatherList));
//                            cacheEditor.apply();
//                        } else {
//                            // 请求失败时的处理
//                            Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    } catch (ExecutionException e) {
//                        throw new RuntimeException(e);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }}
//                    //============================================================================
//
//        refreshButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String cityId = cityIdEditText.getText().toString().trim();
//                if (!cityId.isEmpty()) {
//                    new WeatherTask().execute(cityId);
//                }
//            }
//        });
//    }
//
//    //关注城市
//    private void saveStar() {
//        // 获取输入的城市名
//        String city = cityIdEditText.getText().toString();
//        // 正则表达式验证城市名是否合法：2-10个汉字
//        if (!city.matches("^[\\u4e00-\\u9fa5]{2,10}$")) {
//            Msg.shorts(MainActivity.this, "城市不存在！");
//            return;
//        } else if (city.isEmpty()) {
//            Msg.shorts(MainActivity.this, "请输入城市名");
//            return;
//        }
//        // 获取关注列表
//        if (!starButton.getText().toString().equals("已收藏的城市列表")) {
//            // 获取关注列表
//            // 遍历列表，如果存在则移除
//            for (int i = 0; i < weatherList.size(); i++) {
//                if (weatherList.get(i).equals(city)) {
//                    weatherList.remove(i);
//                    // 将新的关注列表写入缓存
//                    starEditor.putString("city", gson.toJson(new ArrayList<>(new HashSet<>(weatherList))));
//                    starEditor.apply();
//                    Msg.shorts(MainActivity.this, "取关成功");
//                    return;
//                }
//            }
//        }
//        weatherList.add(city);
//        starEditor.putString("city", gson.toJson(new ArrayList<>(new HashSet<>(weatherList))));
//        starEditor.apply();
//        Msg.shorts(MainActivity.this, "关注成功");
//    }
//    private class WeatherTask extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            String cityId = params[0];
//            String requestUrl = BASE_URL + "city=" + cityId + "&key=" + API_KEY + "&extensions=base";
//
//            try {
//                URL url = new URL(requestUrl);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setRequestMethod("GET");
//                connection.setReadTimeout(10000);
//                connection.setConnectTimeout(15000);
//                connection.connect();
//
//                int responseCode = connection.getResponseCode();
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    InputStream inputStream = connection.getInputStream();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                    StringBuilder result = new StringBuilder();
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        result.append(line);
//                    }
//                    bufferedReader.close();
//                    inputStream.close();
//                    return result.toString();
//                } else {
//                    Log.e("WeatherTask", "Error response code: " + responseCode);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            if (result != null) {
//                try {
//                    JSONObject jsonResult = new JSONObject(result);
//                    // 解析JSON数据，提取需要的天气信息
//                    String province = jsonResult.optString("province");
//                    String city = jsonResult.optString("city");
//                    String updateTime = jsonResult.optString("reporttime");
//                    String temperature = jsonResult.optString("daytemp");
//                    String humidity = jsonResult.optString("dayweather");
//
//                    // 在TextView中显示天气信息
//                    String weatherInfo = "省份：" + province + "\n" +
//                            "城市：" + city + "\n" +
//                            "更新时间：" + updateTime + "\n" +
//                            "日间温度：" + temperature + "°C" + "\n" +
//                            "天气：" + humidity + "%";
//                    simpleAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,weatherList);  //配置适配器
//                    listView.setAdapter(simpleAdapter);
//                    /*
//                    TODO:(1)将获取到的JSON代码解析转换成字符，存储到lisview控件中
//                     （2）搜索栏右边添加下拉框，存储最近三次的城市信息
//                     （3）ListView中保存最近的查询信息，右侧添加是否关注按钮
//                     （4）
//                     */
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                // 处理请求失败的情况
//                Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//    private void initButton() {
//        cityIdEditText    = findViewById(R.id.editTextCityId);
//        searchButton   = findViewById(R.id.buttonSearch);
//        refreshButton  = findViewById(R.id.buttonRefresh);
//        starButton = findViewById(R.id.star_btn);
//        listView           = findViewById(R.id.search_weather);
//        reportTime = findViewById(R.id.report_Time);
//        listView.setCacheColorHint(4); // 空间换时间
//    }
//}
//}