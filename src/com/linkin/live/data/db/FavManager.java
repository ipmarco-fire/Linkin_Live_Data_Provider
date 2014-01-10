
package com.linkin.live.data.db;

import android.content.Context;


import java.util.Map;

public class FavManager {
    Context context;

    public FavManager(Context context) {
        this.context = context;
    }

    public Map<String, Boolean> getAll() {
        DBFavHelper dbFav = new DBFavHelper(context);
        dbFav.open();
        Map<String, Boolean> favMap =  dbFav.getAll();
        dbFav.close();
        return favMap;
    }
}
