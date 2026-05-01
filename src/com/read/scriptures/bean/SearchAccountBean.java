package com.read.scriptures.bean;

import java.util.List;
import java.util.Objects;

public class SearchAccountBean {

    /**
     * code : 1
     * msg :
     * time : 1610344172
     * data : [{"sequence":"oBz-wxESQB1XJFnvgAbnRx7bi974","username":"1908644025","status":0,"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/XNE2w7Bx4jSOGOZicAtFEL9ib1DsbKpQicYG3ewezFHBzcN3ej5ficgM3vsntWHM5amTxbkqysSf3hEQWyW983JFsQ/132","nickname":"基列乳香"},{"sequence":"oBz-wxP7WIltOfR5tTzU1ZSwWBVg","username":"1908644024","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxE3exF5mAORhyxFL_SrNgtA","username":"1908644023","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxFJWDllABb2aQEBq0tABUJo","username":"1908644022","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxC3-gTlKOoHGYZiUZKFxeaw","username":"1908644021","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxPE4ip0Jdk5aXwQzYVDxn5g","username":"1908644020","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxFaF8x2TPC1fTUykD0oHOAQ","username":"1908644019","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxFOP_OeZbQQ6jV4SOiXR2RY","username":"1908644018","status":0,"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/FQQA0icGXUvIibINSyrxnxI8w10mN8OO0TCVIZX6MCeukYfUYrFhR0BdKtj492wrts7ibFugCoUOY4icpKn3L8mCRw/132","nickname":"李文"},{"sequence":"oBz-wxIxYf_sSdx7hLB3xhN_pZ8g","username":"1908644017","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxIPIObb2xGrmH-M6t4MEhNQ","username":"1908644016","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxA1-sHEhHezlVz9qt6t8gTU","username":"1908644015","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxNZOjLzAz6YsPUTVlf5hgRs","username":"1908644014","status":0,"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/6e98Diah8ykFe8Uj1PEmfj8MOdGPFGG8RuNia1mlyoGq64jvvwXxFicln5yqp63dJX2jZmhtO2NwoFTPT39oia5LeQ/132","nickname":"朝睿"},{"sequence":"oBz-wxLZYxskg-eUcEzc0JjnYyb0","username":"1908644013","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxP6qhnOhex1iPEDvjiDCkUA","username":"1908644012","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxGj1OQOcajEgKpAFy1eTQT4","username":"1908644011","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxAbiq2tqAtA2MpUPYqRRsMU","username":"1908644010","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxF31hxkba6zIPb7QxRwjdVE","username":"1908644008","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxDDV84qCzbNQKqJBWEkknHE","username":"1908644009","status":0,"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/Q3auHgzwzM6zmwtupibXPA5jcqgicvo3RTN9IhsPGiazbKngNOD19nglibW6HtNTCK3YMTgG3FPgDYQ7OV1feK7liag/132","nickname":"阿白"},{"sequence":"oBz-wxCSfC_TCB-SidXo3Iqwcg0E","username":"1908644007","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxGg7EMfB7bfmemb1bcFaiSs","username":"1908644006","status":0,"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/R6HHfcvA53icNTUR8wxpcrdlhtb6tN8tXd6dYYnWbsjOXfkfcPia2qjL358TJTib5eicxI28dTv4BSBeVJNyJLicVmg/132","nickname":"吕春刚"},{"sequence":"oBz-wxDzsuZgxkr0bOAPx5fYNNtU","username":"1908644005","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxHceHQVgKv_0cnhQQQBYdrs","username":"1908644004","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxJVrkvye2n-HBsByvOIzhuA","username":"1908644003","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxGWSGCohRQXvn_N7hzihSnU","username":"1908644002","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxBTfgJL4gT4RmX9xiPGkzfw","username":"1908644001","status":0,"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJhwr7LrTUgrgWN9FMr5RWBgvbkpabpRqBn8nU1C1GxBWSnd06FLjudOFb6qicMKiaj5VsyofF6EgbA/132","nickname":"gold"},{"sequence":"oBz-wxOWR3PVaY_bfX7RK8vxzhPA","username":"1908644000","status":0,"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eojiayEKYlHI3SSJCmyJN0AT0uRsyfgShbrFiacM0EJG7NoYqosqVJYuuzq7t24bG8mkcnKmoiax2rAA/132","nickname":"海鸥"},{"sequence":"oBz-wxOWqbZscTsSkyL0GWqYKz90","username":"1908643999","status":0,"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/uCZHH1WuAZghveibM7r5AaiaFXvZM7WhVMSkVLWXuXnM4QI3Lps6kicwlm0dxDib3ibFqKE41ZBBUW8cSqXPdStjnmg/132","nickname":"瞭望者"},{"sequence":"oBz-wxLLTsaG2_bOYapdP6g_deGc","username":"1908643998","status":0,"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/VzicJHeZ2cPvFLTuicMYG2XIrrUZ8j9miafGh00jB6lcOYV0pLCATUG5rOUaM6MvibkBA74RCCqXkWHI5IicupLXYZQ/132","nickname":"欢欢"},{"sequence":"oBz-wxDyTPXvKS30159EJ7dODw9o","username":"1908643997","status":0,"avatar":"","nickname":""},{"sequence":"oBz-wxMMmDDK-Da2DrN__NPvMlJk","username":"1908643996","status":0,"avatar":"https://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eow4tiaNxALtv1WAPVIWQibkcSWTRHRgc0oNrBDXMptIicwLbia6oC7VqJaRwPxibBa6Dlr4V2fBtvRMgA/132","nickname":"愛的家園"}]
     */

    public int code;
    public String msg;
    public String time;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * sequence : oBz-wxESQB1XJFnvgAbnRx7bi974
         * username : 1908644025
         * status : 0
         * avatar : https://thirdwx.qlogo.cn/mmopen/vi_32/XNE2w7Bx4jSOGOZicAtFEL9ib1DsbKpQicYG3ewezFHBzcN3ej5ficgM3vsntWHM5amTxbkqysSf3hEQWyW983JFsQ/132
         * nickname : 基列乳香
         */

        public String sequence;
        public String username;
        public int status;
        public String avatar;
        public String nickname;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DataBean)) return false;
            DataBean dataBean = (DataBean) o;
            return Objects.equals(username, dataBean.username);
        }

        @Override
        public int hashCode() {
            return Objects.hash(username);
        }
    }
}
