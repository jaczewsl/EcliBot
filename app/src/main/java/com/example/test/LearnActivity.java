package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.FragmentManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.UUID;

public class LearnActivity extends AppCompatActivity {

    //--------------------------------- VARIABLES --------------------------------------------------

    private final String TYPE_REP = "repeat";
    private final String TYPE_ACT = "action";
    private final String TYPE_NES = "nested";
    private final String TYPE_IFF = "ifs";
    private final String FOR = "for";
    private final String BAC = "bac";
    private final String LEF = "lef";
    private final String RIG = "rig";
    private final String CRE = "cre";
    private final String CGR = "cgr";
    private final String CBL = "cbl";
    private final String CNC = "cnc";
    private final String BON = "bon";
    private final String BOF = "bof";
    private final String REP = "rep";
    private final String REP_END = "rep_end";
    private final String IFF_END = "iff_end";
    private final String IFF_CR = "iff_cr";
    private final String IFF_CG = "iff_cg";
    private final String IFF_CB = "iff_cb";
    private final String IFF_CN = "iff_cn";
    private final String IFF_BO = "iff_bo";
    private final String IFF_BF = "iff_bf";
    private final String IFF_CR_END = "iff_cr_end";
    private final String IFF_CG_END = "iff_cg_end";
    private final String IFF_CB_END = "iff_cb_end";
    private final String IFF_CN_END = "iff_cn_end";
    private final String IFF_BO_END = "iff_bo_end";
    private final String IFF_BF_END = "iff_bf_end";

    // Dialog Fragment variables
    final FragmentManager fm = getSupportFragmentManager();                                 //
    final DialogFragment dialogFragment = new DialogFragment ();                            //

    // connection variables
    String address = "B8:27:EB:BD:37:5C";                                                   // RaspberryPi MAC address
    public BluetoothAdapter myBluetooth = null;                                             // BT adaptor
    BluetoothSocket btSocket = null;                                                        // BT socket
    private boolean isBtConnected = false;                                                  // flag that keeps track on connection status
    static final UUID myUUID = UUID.fromString("1e0ca4ea-299d-4335-93eb-27fcfe7fa848");     // SPP Universally unique identifier - 128-bit number

    // widget variables
    private Button btn_play, btn_clear, btn_help;
    private Button btn_mov, btn_for, btn_bac, btn_lef, btn_rig, btn_ext;
    private Button btn_act, btn_col, btn_colR, btn_colG, btn_colB, btn_colNo;
    private Button btn_buz, btn_buzOn, btn_buzOff, btn_ext2;
    private Button btn_int, btn_rep, btn_if, btn_ifnot;
    private Button btn_if_red, btn_if_green, btn_if_blue, btn_if_no, btn_if_buzz, btn_if_buzz_no;
    private LinearLayout lay_men, lay_mov, lay_third, lay_board;

    // flag variables
    private boolean repFlag = false;        // true when repeat instruction selected
    private boolean iffFlag = false;        // true when if statement selected - used for view choice
    private boolean iffCreFlag = false;     // true when If red selected
    private boolean iffCgrFlag = false;     // true when If green selected
    private boolean iffCblFlag = false;     // true when If blue selected
    private boolean iffCnoFlag = false;     // true when If no colour selected
    private boolean iffBonFlag = false;     // true when If buzzer on selected
    private boolean iffBofFlag = false;     // true when If buzzer off selected

    // list variables
    private ArrayList<ExampleItem> mExampleList;        // list of items/objects/instructions visible to the user that will be displayed in RecyclerView
    private ArrayList<String> instrList;                // list of gathered instructions based on user choices which will be send to server through socket

    // recyclerView variables
    private RecyclerView mRecyclerView;                 // scrolling list of elements will be displayed in it
    private ExampleAdapter mAdapter;                    // the view holder objects are created by Adapter
    private RecyclerView.LayoutManager mLayoutManager;  // layout manager provides views for Recycler View

    //--------------------------------- onCreate STARTS HERE ---------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        getSupportActionBar().setDisplayShowHomeEnabled(true);      // specifies that the Home button is shown
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // specifies whether or not the Home button has the arrow used for Up Navigation next to it

        new ConnectBT().execute();                                  //Call the class to connect

        instrList = new ArrayList<String>();                        // initialising list where all instructions will be send to server through socket
        mExampleList = new ArrayList<>();
        buildRecyclerView();                                        // builds scrolling list of elements which will be displayed in it


        // finds the views from the layout resource file for each element
        btn_help = findViewById(R.id.btn_help);
        btn_play = findViewById(R.id.btn_play);
        btn_clear = findViewById(R.id.btn_clear);
        btn_help = findViewById(R.id.btn_help);

        lay_men = findViewById(R.id.lay_menu);
        btn_mov = findViewById(R.id.btn_move);
        btn_act = findViewById(R.id.btn_action);
        btn_int = findViewById(R.id.btn_inter);

        lay_mov = findViewById(R.id.lay_move);
        btn_for = findViewById(R.id.btn_forward);
        btn_bac = findViewById(R.id.btn_backward);
        btn_lef = findViewById(R.id.btn_left);
        btn_rig = findViewById(R.id.btn_right);
        btn_col = findViewById(R.id.btn_col);
        btn_buz = findViewById(R.id.btn_buz);
        btn_rep = findViewById(R.id.btn_repeat);
        btn_if = findViewById(R.id.btn_if);
        btn_ifnot = findViewById(R.id.btn_ifnot);
        btn_ext = findViewById(R.id.btn_exit);

        lay_third = findViewById(R.id.lay_third);
        btn_colR = findViewById(R.id.btn_red);
        btn_colG = findViewById(R.id.btn_green);
        btn_colB = findViewById(R.id.btn_blue);
        btn_colNo = findViewById(R.id.btn_nocolour);
        btn_buzOn = findViewById(R.id.btn_buzzOn);
        btn_buzOff = findViewById(R.id.btn_buzzOff);
        btn_if_red = findViewById(R.id.btn_if_red);
        btn_if_green = findViewById(R.id.btn_if_green);
        btn_if_blue = findViewById(R.id.btn_if_blue);
        btn_if_no = findViewById(R.id.btn_if_no_colour);
        btn_if_buzz = findViewById(R.id.btn_if_buzz_on);
        btn_if_buzz_no = findViewById(R.id.btn_if_buzz_off);
        btn_ext2 = findViewById(R.id.btn_exit2);

        lay_board = findViewById(R.id.lay_board);


        //--------------------- SETTING BUTTON LISTENERS -------------------------------------------
        // called when ACTION... button clicked
        // manages visual appearance of the elements
        btn_mov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lay_men.setVisibility(View.GONE);
                lay_mov.setVisibility(View.VISIBLE);
                lay_third.setVisibility(View.GONE);

                btn_for.setVisibility(View.VISIBLE);
                btn_bac.setVisibility(View.VISIBLE);
                btn_lef.setVisibility(View.VISIBLE);
                btn_rig.setVisibility(View.VISIBLE);
                btn_col.setVisibility(View.GONE);
                btn_buz.setVisibility(View.GONE);
                btn_rep.setVisibility(View.GONE);
                btn_if.setVisibility(View.GONE);
                btn_ifnot.setVisibility(View.GONE);
                btn_ext.setVisibility(View.VISIBLE);
            }
        });

        // called when ACTION... button clicked
        // manages visual appearance of the elements
        btn_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewMovLayout();

                btn_for.setVisibility(View.GONE);
                btn_bac.setVisibility(View.GONE);
                btn_lef.setVisibility(View.GONE);
                btn_rig.setVisibility(View.GONE);
                btn_col.setVisibility(View.VISIBLE);
                btn_buz.setVisibility(View.VISIBLE);
                btn_rep.setVisibility(View.GONE);
                btn_if.setVisibility(View.GONE);
                btn_ifnot.setVisibility(View.GONE);
                btn_ext.setVisibility(View.VISIBLE);

            }
        });

        // called when INTERACTION... button clicked
        // manages visual appearance of the elements
        btn_int.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewMovLayout();

                btn_for.setVisibility(View.GONE);
                btn_bac.setVisibility(View.GONE);
                btn_lef.setVisibility(View.GONE);
                btn_rig.setVisibility(View.GONE);
                btn_col.setVisibility(View.GONE);
                btn_buz.setVisibility(View.GONE);
                btn_rep.setVisibility(View.VISIBLE);
                btn_if.setVisibility(View.VISIBLE);
                btn_ifnot.setVisibility(View.GONE);
                btn_ext.setVisibility(View.VISIBLE);

            }
        });

        // called when FORWARD button clicked
        btn_for.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInstr(TYPE_ACT, FOR, "Forward", R.drawable.ic_forward);     // insert element to both lists
                viewMenuLayout();                                                       // manages the visual appearance of the elements in layouts
            }
        });

        // called when BACKWARD button clicked
        btn_bac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInstr(TYPE_ACT, BAC, "Backward", R.drawable.ic_backward);   // insert element to both lists
                viewMenuLayout();                                                       // manages the visual appearance of the elements in layouts
            }
        });

        // called when LEFT button clicked
        btn_lef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInstr(TYPE_ACT, LEF, "Left", R.drawable.ic_left);           // insert element to both lists
                viewMenuLayout();                                                       // manages the visual appearance of the elements in layouts
            }
        });

        // called when RIGHT button clicked
        btn_rig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInstr(TYPE_ACT, RIG, "Right", R.drawable.ic_right);         // insert element to both lists
                viewMenuLayout();                                                       // manages the visual appearance of the elements in layouts
            }
        });

        // goes back to previous layout
        btn_ext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewMenuLayout();
            }
        });

        // called when COLOUR button clicked
        // manages visual appearance of the elements
        btn_col.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewThirdLayout();

                btn_colR.setVisibility(View.VISIBLE);
                btn_colG.setVisibility(View.VISIBLE);
                btn_colB.setVisibility(View.VISIBLE);
                btn_colNo.setVisibility(View.VISIBLE);
                btn_buzOn.setVisibility(View.GONE);
                btn_buzOff.setVisibility(View.GONE);
                btn_ext2.setVisibility(View.VISIBLE);
                btn_if_red.setVisibility(View.GONE);
                btn_if_green.setVisibility(View.GONE);
                btn_if_blue.setVisibility(View.GONE);
                btn_if_no.setVisibility(View.GONE);
                btn_if_buzz.setVisibility(View.GONE);
                btn_if_buzz_no.setVisibility(View.GONE);

            }
        });

        // called when BUZZER button clicked
        // manages visual appearance of the elements
        btn_buz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewThirdLayout();

                btn_colR.setVisibility(View.GONE);
                btn_colG.setVisibility(View.GONE);
                btn_colB.setVisibility(View.GONE);
                btn_colNo.setVisibility(View.GONE);
                btn_buzOn.setVisibility(View.VISIBLE);
                btn_buzOff.setVisibility(View.VISIBLE);
                btn_ext2.setVisibility(View.VISIBLE);
                btn_if_red.setVisibility(View.GONE);
                btn_if_green.setVisibility(View.GONE);
                btn_if_blue.setVisibility(View.GONE);
                btn_if_no.setVisibility(View.GONE);
                btn_if_buzz.setVisibility(View.GONE);
                btn_if_buzz_no.setVisibility(View.GONE);

            }
        });

        // called when COLOUR RED button clicked
        btn_colR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInstr(TYPE_ACT, CRE, "LED Red", R.drawable.ic_color_red);           // insert element to both lists
                viewMenuLayout();                                                               // manages the visual appearance of the elements in layouts
            }
        });

        // called when COLOUR GREEN button clicked
        btn_colG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInstr(TYPE_ACT, CGR, "LED Green", R.drawable.ic_color_green);      // insert element to both lists
                viewMenuLayout();                                                               // manages the visual appearance of the elements in layouts
            }
        });

        // called when COLOUR BLUE button clicked
        btn_colB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInstr(TYPE_ACT, CBL, "LED Blue", R.drawable.ic_color_blue);         // insert element to both lists
                viewMenuLayout();                                                               // manages the visual appearance of the elements in layouts
            }
        });

        // called when NO COLOUR button clicked
        btn_colNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInstr(TYPE_ACT, CNC, "LED Off", R.drawable.ic_color_no);        // insert element to both lists
                viewMenuLayout();                                                           // manages the visual appearance of the elements in layouts
            }
        });

        // called when BUZZER ON button clicked
        btn_buzOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInstr(TYPE_ACT, BON, "Buzzer On", R.drawable.ic_buzz_on);       // insert element to both lists
                viewMenuLayout();                                                           // manages the visual appearance of the elements in layouts
            }
        });

        // called when BUZZER OFF button clicked
        btn_buzOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInstr(TYPE_ACT, BOF, "Buzzer Off", R.drawable.ic_buzz_off);         // insert element to both lists
                viewMenuLayout();                                                               // manages the visual appearance of the elements in layouts
            }
        });

        // called when IF RED ON button clicked
        btn_if_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInstr(TYPE_IFF, IFF_CR, "IF LED Red", R.drawable.ic_iff);           // insert element to both lists
                iffCreFlag = true;                                                              // raise the appropriate flag
                viewMenuLayout();                                                               // manages the visual appearance of the elements in layouts
            }
        });

        // called when IF GREEN ON button clicked
        btn_if_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInstr(TYPE_IFF, IFF_CG, "IF LED Green", R.drawable.ic_iff);         // insert element to both lists
                iffCgrFlag = true;                                                              // raise the appropriate flag
                viewMenuLayout();                                                               // manages the visual appearance of the elements in layouts
            }
        });

        // called when IF BLUE button clicked
        btn_if_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInstr(TYPE_IFF, IFF_CB, "IF LED Blue", R.drawable.ic_iff);          // insert element to both lists
                iffCblFlag = true;                                                              // raise the appropriate flag
                viewMenuLayout();                                                               // manages the visual appearance of the elements in layouts
            }
        });

        // called when IF BUZZER ON button clicked
        btn_if_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInstr(TYPE_IFF, IFF_CN, "IF LED Off", R.drawable.ic_iff);           // insert element to both lists
                iffCnoFlag = true;                                                              // raise the appropriate flag
                viewMenuLayout();                                                               // manages the visual appearance of the elements in layouts
            }
        });

        // called when IF BUZZER ON button clicked
        btn_if_buzz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInstr(TYPE_IFF, IFF_BO, "IF Buzzer On", R.drawable.ic_iff);        // insert element to both lists
                iffBonFlag = true;                                                             // raise the appropriate flag
                viewMenuLayout();                                                              // manages the visual appearance of the elements in layouts
            }
        });

        // called when IF BUZZER OFF button clicked
        btn_if_buzz_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertInstr(TYPE_IFF, IFF_BF, "IF Buzzer Off", R.drawable.ic_iff);      // insert element to both lists
                iffBofFlag = true;                                                            // raise the appropriate flag
                viewMenuLayout();                                                             // manages the visual appearance of the elements in layouts
            }
        });

        // goes back to previous layout
        btn_ext2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewMovLayout();                                                                // manages the visual appearance of the elements in layouts
            }
        });


        // called when repeat button pressed
        btn_rep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogFragment.showNow(fm, "Loop statement");                                       // Dialog Fragment shown to the user

                Log.d("--- LEARN --", "How many times: " + dialogFragment.getIterationNum());   // log created

                insertInstr(TYPE_REP, REP, "Repeat", R.drawable.ic_loop);                         // element inserted to the list of instructions with appropriate arguments
                viewMenuLayout();                                                                       // managing layout
            }
        });

        // manages the visual appearance of the elements in layouts
        btn_if.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewThirdLayout();

                btn_colR.setVisibility(View.GONE);
                btn_colG.setVisibility(View.GONE);
                btn_colB.setVisibility(View.GONE);
                btn_colNo.setVisibility(View.GONE);
                btn_buzOn.setVisibility(View.GONE);
                btn_buzOff.setVisibility(View.GONE);
                btn_ext2.setVisibility(View.VISIBLE);
                btn_if_red.setVisibility(View.VISIBLE);
                btn_if_green.setVisibility(View.VISIBLE);
                btn_if_blue.setVisibility(View.VISIBLE);
                btn_if_no.setVisibility(View.VISIBLE);
                btn_if_buzz.setVisibility(View.VISIBLE);
                btn_if_buzz_no.setVisibility(View.VISIBLE);
            }
        });


        // clears both lists and resets the flags
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExampleList.clear();               // list of visual objects set to empty
                instrList.clear();                  // list of instructions set to empty
                mAdapter.notifyDataSetChanged();    // refreshes the view - all elements are gone from the recycler View
                buildRecyclerView();                // building RecyclerView once more
                repFlag = false;                    // flags are reset
                iffFlag = false;
            }
        });

        // when play button is clicked this method is called; all the instructions/commands provided/chosen
        // by the user are changed into string and send to the server through socket
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder sendData = new StringBuilder();
                String s = "";

                if(instrList.size() > 0){                                           // appends all instructions from the list to the StringBuilder
                    for(int i=0; i < instrList.size(); i++){
                        sendData = sendData.append(instrList.get(i));
                        sendData = sendData.append(",");
                    }

                    s = sendData.substring(0, sendData.length() - 1);    // last , (comma) deleted from the list

                    Log.d("----- SEND DATA", s);                                // log created
                }

                if (btSocket!=null) {
                    try {
                        OutputStreamWriter out = new OutputStreamWriter(btSocket.getOutputStream());

                        out.write("DATA");                                          // send "DATA" String to server
                        out.flush();                                                    // flushes the output stream and forces any buffered output bytes to be written out

                        out.write(s);                                                   // !!!! send String created from list !!! - INSTRUCTIONS THAT WILL BE EXECUTED BY ECLIBOT -
                        out.flush();                                                    // flushes the output stream and forces any buffered output bytes to be written out
                    }
                    catch (IOException e) {
                        msg("Error");                                                 // Toast thrown when error occurs during data streaming
                    }
                }

                //FOR TESTING
                Log.d("------ instrList SIZE: "," " + instrList.size());        // log created
                Log.d("-----mExamleList SIZE: "," " + mExampleList.size());     // log created

            }
        });

        // when help button is clicked
        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        //--------------------- SETTING BUTTON LISTENERS ENDS HERE ---------------------------------

    }
    //--------------------------------- onCreate ENDS HERE ---------------------------------------


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == android.R.id.home){                                                            // if return button clicked

            if (btSocket!=null)                                                                 // ff the btSocket is busy
            {
                try
                {
                    btSocket.getOutputStream().write("DISCONNECT".getBytes());                  // send "DISCONNECT" String to server
                    btSocket.close();                                                           // close socket
                    new ConnectBT().cancel(true);                               // close connection
                }
                catch (IOException e) {
                    msg("Error");
                }
            }
            this.finish();                                                                      // return to the previous Activity -> MenuActivity
            Intent myIntent = new Intent(LearnActivity.this, MenuActivity.class);
            LearnActivity.this.startActivity(myIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    // Insert items to the list;
    public void insertInstr(String type, String button, String display, int drawable){

        if(type.equalsIgnoreCase(TYPE_REP)){                        // checks the type of the view
            if(repFlag || iffFlag){                                 // checks flag
                Toast.makeText(this, "Can't create nested instruction! Close existing one.", Toast.LENGTH_LONG).show();     // nested loops are not allowed in this iteration of the app
            }else{ // REPEAT VIEW
                instrList.add(button);                              // adds the instruction to the list
                repFlag = true;                                     // raise the flag that loop has started
                int position = mExampleList.size();                 // position pointer assigned
                insertItem(position, display, TYPE_REP, drawable);  // calls method with all the parameters
                Log.d("-- LEARN ", "item added: " + TYPE_REP);      // Log created
            }
        }
        else if(type.equalsIgnoreCase(TYPE_IFF)){                   // checks the type of the view
            if(repFlag || iffFlag){                                 // checks flag
                Toast.makeText(this, "Can't create nested instruction! Close existing one.", Toast.LENGTH_LONG).show();
            }else{ // IF VIEW
                instrList.add(button);                              // adds the instruction to the list
                iffFlag = true;                                     // raise the flag that if statement has started
                int position = mExampleList.size();                 // position pointer assigned
                insertItem(position, display, TYPE_IFF, drawable);  // calls method with all the parameters
                Log.d("-- LEARN ", "item added: " + TYPE_IFF);      // Log created
            }

        }
        else if(type.equalsIgnoreCase(TYPE_ACT)){                           // checks the type of the view
            // NESTED VIEW
            if(repFlag || iffFlag){                                         // checks flag
                instrList.add(button);                                      // adds the instruction to the list
                int position = mExampleList.size();                         // position pointer assigned
                insertItem(position, display, TYPE_NES, drawable);          // calls method with all the parameters
                Log.d("-- LEARN ", "item added: " + TYPE_NES);      // Log created
            }
            else{ // ACTION VIEW
                instrList.add(button);                                      // adds the instruction to the list
                int position = mExampleList.size();                         // position pointer assigned
                insertItem(position, display, TYPE_ACT, drawable);          // calls method with all the parameters
                Log.d("-- LEARN ", "item added: " + TYPE_ACT);      // Log created
            }
        }
    }

    // inserts item visible to user with position, label, type of the instruction (loop, if, action)
    // and resource id - is responsible to bringing the right icon from drawable folder
    public void insertItem(int position, String instr, String type, int resource) {
        mExampleList.add(position, new ExampleItem(resource, instr, type));         // adds element to the list of visual objects
        mAdapter.notifyItemInserted(position);                                      // refreshes the view
    }

    // removes item from list of visual objects, then calls the method which removes
    // corresponding item from the instructions list
    public void removeItem(int position) {
        mExampleList.remove(position);
        mAdapter.notifyItemRemoved(position);

        removeFromList(position);
    }

    // removes item from list of instructions
    public void removeFromList(int position){
        instrList.remove(position);
    }


    // used for changing items
    public void changeItem(int position, String text) {
        mExampleList.get(position).changeText1(text);
        mAdapter.notifyItemChanged(position);
    }

    // called when user ends the repeat statement by pressing the arrow image button
    public void endRepeat(int position, String text) {
//        mExampleList.get(position).changeText1(text);
//        mAdapter.notifyItemChanged(position);

        int j = dialogFragment.getIterationNum();                               // value from Dialog Fragment assigned to variable j
        Log.d("++++", "---- " + j);                                     // Log created
        mExampleList.get(position).changeText1("Repeat x " + j + " times");     // text changed on the view showing how many times loop will be executed
        mAdapter.notifyDataSetChanged();                                        // refreshes the view
        instrList.add(Integer.valueOf(j).toString());                           // instruction value changed to String and then added to the list
        instrList.add(REP_END);                                                 // notification END OF LOOP added also to the list
        repFlag = false;                                                        // flag reset, new loop might be started by the user
        Log.d("-- LEARN If_end --", "size: " + instrList.size());      // Log created
    }

    // return appropriate if statement symbols
    public String getIffEnd(){
        if(iffCreFlag)
            return IFF_CR_END;
        else if(iffCgrFlag)
            return IFF_CG_END;
        else if(iffCblFlag)
            return IFF_CB_END;
        else if(iffCnoFlag)
            return IFF_CN_END;
        else if(iffBonFlag)
            return IFF_BO_END;
        else if(iffBofFlag)
            return IFF_BF_END;
        else return IFF_END;
    }

    // clear the if flags
    public void releaseIffFlags(){
        iffCreFlag = false;
        iffCgrFlag = false;
        iffCblFlag = false;
        iffCnoFlag = false;
        iffBonFlag = false;
        iffBofFlag = false;
    }

    // called when user ends the if statement by pressing the arrow image button
    public void finishIF(int position, String text) {
//        mExampleList.get(position).changeText1(text);
//        mAdapter.notifyItemChanged(position);

        instrList.add(getIffEnd());                     // appropriate symbol added to list
        releaseIffFlags();                              // if flags released
        iffFlag = false;
        Log.d("-- LEARN If_end --", "size: " + instrList.size());  // Log created
    }


    // --------------------- BUILDING RECYCLER VIEW ------------------------------------------------
    public void buildRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(mExampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);



        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {

            // WILL BE USED WHEN ITEM FROM LIST IS CLICKED - In future iteration of the app
//            @Override
//            public void onItemClick(int position) {
//                changeItem(position, "Clicked");
//            }

            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }

            @Override
            public void onRepClick(int position) {
                endRepeat(position, "Button was pressed");
            }

            @Override
            public void onIfClick(int position) {
                finishIF(position, "Button was pressed");
            }

        });
    }
    // --------------------- ENDS - RECYCLER VIEW --------------------------------------------------

    public void viewMenuLayout(){                   // sets lay_menu layout visible - three buttons on the very left
        lay_men.setVisibility(View.VISIBLE);        // MOVE, ACTION, INTERACTION
        lay_mov.setVisibility(View.GONE);
        lay_third.setVisibility(View.GONE);
    }

    public void viewMovLayout(){                    // sets lay_mov layout visible - three buttons on the very left
        lay_men.setVisibility(View.GONE);           //
        lay_mov.setVisibility(View.VISIBLE);
        lay_third.setVisibility(View.GONE);
    }

    public void viewThirdLayout(){                   // sets lay_third layout visible - three buttons on the very left
        lay_men.setVisibility(View.GONE);
        lay_mov.setVisibility(View.GONE);
        lay_third.setVisibility(View.VISIBLE);
    }

    // method to use Toast Messaging system
    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }


    //------------------------ ConnectBT class STARTS HERE -----------------------------------------

    private class ConnectBT extends AsyncTask<Void, Void, Void> {                // UI thread

        private boolean ConnectSuccess = true;                                   // if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            Log.d("-- LEARN ACTIVITY --", "onPreExecute");              // Should I check anything before the connection?
        }

        @Override
        protected Void doInBackground(Void... devices) {                                    // while the progress dialog is shown, the connection is done in background
            Log.d("-- LEARN ACTIVITY --", "doInBackground started");

            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();                      // get the mobile bluetooth device
                    BluetoothDevice remote = myBluetooth.getRemoteDevice(address);           // connects to the device's address and checks if it's available
                    btSocket = remote.createInsecureRfcommSocketToServiceRecord(myUUID);     // create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();                  // cancel the current device discovery process
                    btSocket.connect();                                                      //start connection
                }
                if(isCancelled()){
                    Log.d("-- LEARN ACTIVITY --", "Canceled triggered");
                }
            }
            catch (IOException e) {
                ConnectSuccess = false;                                                      // if the try failed, you can check the exception here
                Log.d("-- LEARN ACTIVITY --","Exception thrown");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {                                          //after the doInBackground, it checks if everything went fine
            Log.d("-- LEARN ACTIVITY --", "onPostExcute started");
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");              // when problem occurs with connection a Toast is thrown
                finish();
            }
            else {
                msg("Learn Mode Entered");                                                // when connection successful Train Mode is Shown through Toast
                isBtConnected = true;
            }
        }
    }

}