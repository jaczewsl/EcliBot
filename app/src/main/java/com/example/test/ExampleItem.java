package com.example.test;

public class ExampleItem {                                              // class used for holding object values for RecyclerView
    private int mImageResource;
    private String mText1;
    private String dataType;

    public ExampleItem(int imageResource, String text1, String dType){
        mImageResource = imageResource;                                 // holds the image displayed in every ExampleItem
        mText1 = text1;                                                 // holds String text action label
        dataType = dType;                                               // distinguish between action, loop, and if
    }

    public void changeText1(String text) {                              // changes String value of the item
        mText1 = text;
    }

    public int getImageResource() {                                     // getting Image Resource from R repository (int value)
        return mImageResource;
    }

    public String getText1() {                                          // getting String text from item
        return mText1;
    }

    public String getDataType(){                                        // getting data type (action, loop, if)
        return dataType;
    }
}
