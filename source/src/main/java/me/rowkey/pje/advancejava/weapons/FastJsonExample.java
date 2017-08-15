package me.rowkey.pje.advancejava.weapons;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import me.rowkey.pje.common.meta.User;

/**
 * Created by Bryant.Hang on 2017/8/6.
 */
public class FastJsonExample {

    public static void main(String[] args) {
        User user = new User();
        user.setName("testUser");
        user.setGender("M");
        user.setNickName("nickTest");

        SerializeConfig config = new SerializeConfig();
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        String str = JSON.toJSONString(user, config);
        System.out.println(str);

        user = JSON.parseObject(str, User.class);

        JSONObject jo = JSON.parseObject("{\"name\":\"test\"}");
        String name = jo.getString("name");
        String nick = jo.getString("nickName");

        System.out.println(nick);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "test");

        String jsonStr = "{\"name\":\"testName\",\"interests\":[\"music\",\"basketball\"]," +
                "\"notes\":[{\"title\":\"note1\",\"contentLength\":200},{\"title\":\"note2\",\"contentLength\":100}]}";
        JSONObject jsonObject1 = JSON.parseObject(jsonStr);
        System.out.println(JSONPath.eval(jsonObject1, "$.interests.size()"));
        System.out.println(JSONPath.eval(jsonObject1, "$.interests[0]"));
        System.out.println(JSONPath.eval(jsonObject1, "$.notes[contentLength > 100].title"));
        System.out.println(JSONPath.eval(jsonObject1, "$.notes['title']"));
    }
}
