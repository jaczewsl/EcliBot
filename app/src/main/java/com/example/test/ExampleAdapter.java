package com.example.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;


public class ExampleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ExampleItem> mExampleList;        // stores ExampleItem objects - visual commands chosen by user
    private OnItemClickListener mListener;              // listener that will be assigned to buttons in different views

    private static final int LAYOUT_ONE= 0;             // There are different views for different user choices (different colours, layout and buttons)
    private static final int LAYOUT_TWO= 1;             // 0. Action - will display single action like from moves (forward etc.), LED colours(red, blue etc.) and buzzer (on/off)
    private static final int LAYOUT_THREE= 2;           // 1. Repeat - will display view with corresponding label and button that needs to be pressed by user to end the loop
    private static final int LAYOUT_FOUR= 3;            // 2. Nested - will displayed all action (moves, colour changes, buzzer) in form off nested commands
                                                        // 3. If     - will display view with corresponding label and button that needs to be pressed by user to end the if statement

    // methods needs to be implemented in LearnActivity class
    public interface OnItemClickListener {
//        void onItemClick(int position);               // for future reference; when item from list is clicked

        void onDeleteClick(int position);

        void onRepClick(int position);

        void onIfClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    // This ViewHolder is going to be used When FORWARD, BACKWARD, LEFT, RIGHT, COLOURS(R,G,B,NO)
    // and BUZZER ON/OFF is chosen outside the the loop or if statement
    //----------------------------------------------------------------------------------------------
    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public ImageView mDeleteImage;

        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mDeleteImage = itemView.findViewById(R.id.image_delete);

            // sets click listener for delete ImageView
            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }
    // ---------------------------------------------------------------------------------------------


    // This ViewHolder is going to be used when one of the IF statement is used
    // ---------------------------------------------------------------------------------------------
    public static class IfViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public Button mButton;
        public ImageView mDeleteImage;

        public IfViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.img_view1_if);
            mTextView1 = itemView.findViewById(R.id.txt_view2_if);
            mButton = itemView.findViewById(R.id.btn_view_if);
            mDeleteImage = itemView.findViewById(R.id.img_view2_if);

            // sets click listener for delete ImageView
            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });

            // sets click listener for closing the if statement, after that button becomes invisible
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onIfClick(position);
                            mButton.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            });
        }
    }
    // ---------------------------------------------------------------------------------------------


    // This ViewHolder is going to be used on all action buttons (MOVES, COLOURS, BUZZER) inside
    // the loop or if statement
    // ---------------------------------------------------------------------------------------------
    public static class NestedViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;

        public NestedViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.img_view1_nest2);
            mTextView1 = itemView.findViewById(R.id.txt_view2_nest2);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (listener != null) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            listener.onItemClick(position);
//                        }
//                    }
//                }
//            });
        }
    }
    // ---------------------------------------------------------------------------------------------


    // This ViewHolder is going to be used when repeat loop is chosen
    // ---------------------------------------------------------------------------------------------
    public static class RepeatViewHolder extends RecyclerView.ViewHolder{// implements AdapterView.OnItemSelectedListener {
        public ImageView mImageView;
        public TextView mTextView1;
        public ImageView mDeleteImage;
        public Button mButton;

        public RepeatViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.img_view1);
            mTextView1 = itemView.findViewById(R.id.txt_view1);
            mDeleteImage = itemView.findViewById(R.id.img_view2);
            mButton = itemView.findViewById(R.id.btn_rep);


            // WILL BE USED IN FUTURE - AN ACTION WILL BE TAKEN WHEN AN ITEM FROM LIST IS PRESSED
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (listener != null) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            listener.onItemClick(position);
//                        }
//                    }
//                }
//            });

            // sets click listener for delete ImageView
            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });

            // sets click listener for closing the repeat loop statement, after that button becomes invisible
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onRepClick(position);
                            mButton.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            });
        }
    }


    public ExampleAdapter(ArrayList<ExampleItem> exampleList) {
        mExampleList = exampleList;
    }


    // this method is called automatically by onCreateViewHolder
    // distinguishes what layout should be used according to user choices
    @Override
    public int getItemViewType(int position) {

        if(mExampleList.get(position).getDataType().equalsIgnoreCase("action")) {
            return LAYOUT_ONE;
        }
        if(mExampleList.get(position).getDataType().equalsIgnoreCase("repeat")) {
            return LAYOUT_TWO;
        }
        if(mExampleList.get(position).getDataType().equalsIgnoreCase("nested")){
            return LAYOUT_THREE;
        }
        else{
            return LAYOUT_FOUR;
        }
    }

    // This method is called by the layout manager. This method needs to construct a RecyclerView.ViewHolder
    // and set the view it uses to display its contents. viewType argument is taken from getItemViewType method (above)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        switch(viewType) {                          // getItemViewType() method provides int value based on user choice
            case 0:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item, parent, false);    // exampleView is inflated
                viewHolder = new ExampleViewHolder(v, mListener);
                break;
            case 1:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.repeat_item, parent, false);    // repeatView is inflated
                viewHolder = new RepeatViewHolder(v1, mListener);
                break;
            case 2:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.nested2_item, parent, false);   // nestedView is inflated
                viewHolder = new NestedViewHolder(v2, mListener);
                break;
            case 3:
                View v3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.ifs_item, parent, false);       // ifView is inflated
                viewHolder = new IfViewHolder(v3, mListener);
                break;
            default:
//                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item, parent, false);
//                evh = new ExampleViewHolder(v, mListener);
                break;
        }

        return viewHolder;      // appropriate viewHolder is then returned
    }


    // the layout manager then binds the view holder to its data
    // this method needs to fetch the appropriate data, and use it to fill in the view holder's layout
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ExampleItem currentItem = mExampleList.get(position);

        if(holder.getItemViewType()== LAYOUT_ONE)   // what layout to bind
        {
            ((ExampleViewHolder) holder).mImageView.setImageResource(currentItem.getImageResource());   // setting the image
            ((ExampleViewHolder) holder).mTextView1.setText(currentItem.getText1());                    // setting the text
        }
        else if(holder.getItemViewType()== LAYOUT_TWO){

            ((RepeatViewHolder) holder).mImageView.setImageResource(currentItem.getImageResource());
            ((RepeatViewHolder) holder).mTextView1.setText(currentItem.getText1());
        }
        else if(holder.getItemViewType()== LAYOUT_THREE){
            ((NestedViewHolder) holder).mImageView.setImageResource(currentItem.getImageResource());
            ((NestedViewHolder) holder).mTextView1.setText(currentItem.getText1());
        }
        else if(holder.getItemViewType()== LAYOUT_FOUR){
            ((IfViewHolder) holder).mImageView.setImageResource(currentItem.getImageResource());
            ((IfViewHolder) holder).mTextView1.setText(currentItem.getText1());
        }

    }

    // returns size of the ArrayList which is th number of elements inside - instructions for EcliBot
    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}