package com.wraith.wraithled.classes;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.wraith.wraithled.R;

import java.util.ArrayList;

public class MoodCardGenerator
{
    private LinearLayout row;
    private final CardView[] card;
    private final ImageView[] cardIcon;
    private final ConstraintLayout parent;
    private final RelativeLayout[] cardContent;

    private final int items;
    private final int startidx;
    private final Context context;
    private final ArrayList<Mood> mood;

    public MoodCardGenerator(Context context, ConstraintLayout parent, ArrayList<Mood> moodList, int startidx)
    {
        this.parent = parent;
        this.mood = moodList;
        this.context = context;
        this.startidx = startidx;
        this.items = moodList.get(startidx).isStack() ? 2 : 1;

        this.card = new CardView[this.items];
        this.cardIcon = new ImageView[this.items];
        this.cardContent = new RelativeLayout[this.items];
    }

    public void createLayout(int topToBottom)
    {
        row = new LinearLayout(context);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(Utils.convertDPToPx(context, Utils.CARD_LAYOUT_WIDTH), Utils.convertDPToPx(context, Utils.CARD_LAYOUT_HEIGHT));

        if(startidx == 0)
            layoutParams.topMargin = Utils.convertDPToPx(this.context, 20);

        layoutParams.topToBottom = topToBottom;
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;

        row.setId(Utils.generateID());
        row.setLayoutParams(layoutParams);
        row.setOrientation(LinearLayout.HORIZONTAL);

        createCard();
    }
    private void createCard()
    {
        int margin = Utils.convertDPToPx(context, 20);

        for(int i = 0; i < this.items; i++)
        {
            card[i] = new CardView(context);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);

            cardParams.weight = 1;
            cardParams.setMargins(margin, margin, margin, margin);

            card[i].setFocusable(true);
            card[i].setClickable(true);
            card[i].setId(Utils.generateID());
            card[i].setLayoutParams(cardParams);
            mood.get(this.startidx + i).setViewID(card[i].getId());
            card[i].setBackgroundColor(context.getResources().getColor(R.color.cardItem));

            row.addView(card[i]);
        }
        createCardContent();
    }
    private void createCardContent()
    {
        for(int i = 0; i < this.items; i++)
        {
            cardContent[i] = new RelativeLayout(context);

            RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            cardContent[i].setLayoutParams(contentParams);

            card[i].addView(cardContent[i]);
        }
        createCardIcon();
    }
    private void createCardIcon()
    {
        for(int i = 0; i < this.items; i++)
        {
            cardIcon[i] = new ImageView(context);
            RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(Utils.convertDPToPx(context, Utils.CARD_LAYOUT_ICON_SIZE), Utils.convertDPToPx(context, Utils.CARD_LAYOUT_ICON_SIZE));

            iconParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            iconParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            iconParams.topMargin = Utils.convertDPToPx(context, Utils.CARD_LAYOUT_ICON_MARGIN);

            cardIcon[i].setId(Utils.generateID());
            cardIcon[i].setLayoutParams(iconParams);
            cardIcon[i].setImageResource(mood.get(this.startidx + i).getIconID());

            cardContent[i].addView(cardIcon[i]);
        }
        createCardName();
    }
    private void createCardName()
    {
        for(int i = 0; i < this.items; i++)
        {
            TextView iconName = new TextView(context);
            RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            nameParams.addRule(RelativeLayout.BELOW, getCardIconID(i));
            nameParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            nameParams.topMargin = Utils.convertDPToPx(context, 20);

            iconName.setText(mood.get(this.startidx + i).getNameID());
            iconName.setTextColor(Color.WHITE);
            iconName.setLayoutParams(nameParams);
            iconName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            cardContent[i].addView(iconName);
        }

        this.parent.addView(getRow());
    }
    public int getRowID() { return this.row.getId(); }
    public CardView[] getCards() { return this.card; }

    private LinearLayout getRow() { return this.row; }
    private int getCardIconID(int idx) { return this.cardIcon[idx].getId(); }
}
