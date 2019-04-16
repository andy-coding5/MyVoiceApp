package com.e2excel.myvoice;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class BottomManuHelper {

    public static void showBadge(Context context, BottomNavigationView
            bottomNavigationView, @IdRes int itemId, String value) {
        removeBadge(bottomNavigationView, itemId);
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        View badge = LayoutInflater.from(context).inflate(R.layout.badge_layout, itemView, true);

        TextView text = badge.findViewById(R.id.notifications);
        text.setText(value);
        itemView.addView(badge);
    }

   /* public static void removeBadge(BottomNavigationView bottomNavigationView, @IdRes int itemId) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(R.id.notifications_menu_item);

        if (itemView != null)
            itemView.removeViewAt(2);

    }*/

    public static void removeBadge(BottomNavigationView navigationView,  @IdRes int itemId) {
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(itemId);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;

        Log.v("child", "child count: " + itemView.getChildCount());
        itemView.removeViewAt(2);
    }
}
