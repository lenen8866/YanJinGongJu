import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;

public class APP {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        Class.forName("org.sqlite.JDBC");
        Connection connect = DriverManager.getConnection("jdbc:sqlite:../com.lgmshare.hudong/assets/hudong.db");
        Statement stmt = connect.createStatement();
        ResultSet rs = stmt.executeQuery("select volumeId,content from chapter where name like '%jieshao%'");
        JSONArray introList = new JSONArray();
        while (rs.next()) {
            JSONObject introBean = new JSONObject();
            introBean.put("id", rs.getInt("volumeId"));
            introBean.put("intro", rs.getString("content"));
            introList.add(introBean);
        }
        rs.close();
        stmt.close();
        connect.close();

        System.out.println("introList: " + introList.size());
        String introListStr = JSON.toJSONString(introList);
        Files.write(Paths.get("../com.lgmshare.hudong/assets/getIntroList.json"), introListStr.getBytes(StandardCharsets.UTF_8), new StandardOpenOption[]{StandardOpenOption.CREATE});

        System.out.println("数据生成成功");
    }
}
