package com.poop.rumi.rumi.ocr;

/**
 * Created by Steve on 4/10/2018.
 */



/**
 * Created by Steve on 4/10/2018.
 */
import com.poop.rumi.rumi.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.zip.CheckedOutputStream;

// extrends the ArrayAdapter and pass in a Transaction Object
//          extends ArrayAdapter<Transaction>
// need a TAG and a Context (dont forget to import them)
//      import android.content.Context;
//      import android.widget.ArrayAdapter;
//
// get a constructor ALT + INSERT:
//      use the 5th one: ArrayAdapter(Context, Resource, Object List<T>)
//      change the List<Tracsaction> to ArrayList<Transaction>
//      dont forget to import ArrayList class
// After that the error in MainA go away:
//      take in the context, custom layout that made, arraylist(which is transactionList)
//      TransactionListAdapter adapter = new TransactionListAdapter(this, R.layout.adapter_view_layout, transactionList);
//      listViewItems.setAdapter(adapter);
// Alt Insert/Override Methods/getView (delete super)
//      This will get the view and attach it to the listview we have
// Bring in names, items, prices string
// Create a transaction object to hold these strings
// Create LayoutInflater inflater = LayoutInflater.from(mContext);
// Create a gobal mresource var to use it in a diff. method, dont forget to get it from
//      TransactionListAdapter           mResource = resource;


public class TransactionListAdapter extends ArrayAdapter<Transaction> {

    private static final String TAG = "TransactionListAdapter";
    private Context mContext;
    int mResource;
    ArrayList<Transaction> transactionList;

    RecyclerViewAdapter nameListAdapter;

    int size = 0;


     TextView tvItem ;
     TextView tvPrice ;
     TextView tvNames ;

    LinearLayout linearLayout1;
    LinearLayout linearLayout2;


    public TransactionListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Transaction> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        transactionList =  objects;
        this.size = objects.size();
    }


    // Alt Insert/Override Methods/getView
    // This will get the view and attach it to the listview we have
    // Bring in names, items, prices string
    // Create a transaction object to hold these strings
    // LayoutInflater inflater = LayoutInflater.from(mContext);

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        // Get transaction information
        final String item = getItem(position).getItem();
        final ArrayList<String> names = getItem(position).getNames();
        final Float price = getItem(position).getPrice();


        // Create a transaction object to hold these strings
        Transaction transaction = new Transaction(item, price);

        // Create layoutinflatter, take convertView from the getView
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        // Declare TextView objects:
        // In Main: don't need to call the View, but in here yes: convertView
        final TextView tvItem = (TextView) convertView.findViewById(R.id.textView1);
        final TextView tvPrice = (TextView) convertView.findViewById(R.id.textView3);
        final TextView tvNames = (TextView) convertView.findViewById(R.id.textView2);

        final LinearLayout linearLayout = (LinearLayout)convertView.findViewById(R.id.parent_layout_item_price);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, transactionList.get(position).getItem()+", "+transactionList.get(position).getPrice() , Toast.LENGTH_SHORT).show();

                if(nameListAdapter.getPosOfName() != -1) {

                    Toast.makeText(mContext, "Tryna add name: " + nameListAdapter.getLastNameTapped(), Toast.LENGTH_SHORT).show();

                    if (names.contains(nameListAdapter.getLastNameTapped())) {

                        transactionList.get(position).removeName(nameListAdapter.getLastNameTapped());
                        if(names.size() <= 5)
                            tvNames.setText(names.toString());
                        else if(names.size() > 5)
                            tvNames.setText(names.size()+" people shared this");

                        // The colors aren't working perfectly because they disappeared after scrolling up or down.
                        // comment out for now, will come back later:
                        tvItem.setBackgroundColor(Color.rgb(153,204,255));
                        tvNames.setBackgroundColor(Color.rgb(153,204,255));
                        tvPrice.setBackgroundColor(Color.rgb(153,204,255));

                    }
                    else{

                        transactionList.get(position).addName(nameListAdapter.getLastNameTapped());
//                        tvNames.setText(names.toString());

                        if(names.size() <= 5)
                            tvNames.setText(names.toString());
                        else if(names.size() > 5)
                            tvNames.setText(names.size()+" people shared this");

                        tvItem.setBackgroundColor(Color.rgb(87,188,150));
                        tvNames.setBackgroundColor(Color.rgb(87,188,150));
                        tvPrice.setBackgroundColor(Color.rgb(87,188,150));
                    }



                }

            }
        });


        Button dotsButton = (Button) convertView.findViewById(R.id.threeDotsButton);
        dotsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: create function for this instead
                // openEditItemDialog();

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                LayoutInflater inflater = LayoutInflater.from(mContext);

                final View dialogView = inflater.inflate(R.layout.add_or_edit_item_dialog,null);

                builder.setView(dialogView);


                final EditText editText_item_name = (EditText) dialogView.findViewById(R.id.edit_item_name);
                final EditText editText_item_price = (EditText) dialogView.findViewById(R.id.edit_item_price);

                editText_item_name.setSelection(editText_item_name.getText().length());

                editText_item_name.setText(item);
                editText_item_price.setText(price.toString());

                Button btn_positive = (Button) dialogView.findViewById(R.id.dialog_positive_btn);

                // Create the alert dialog
                final AlertDialog dialog = builder.create();

                // Set the positive button
                btn_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        System.out.println("Item Name: "+editText_item_name.getText().toString());
                        System.out.println("Price: "+editText_item_price.getText().toString());

                        transactionList.get(position).setItem(editText_item_name.getText().toString());
                        transactionList.get(position).setPrice(Float.parseFloat(editText_item_price.getText().toString()));

                        tvItem.setText(editText_item_name.getText().toString());
                        tvPrice.setText("$" + editText_item_price.getText().toString());



                        dialog.dismiss();
                    }
                });


                // Finally, display the alert dialog
                dialog.show();

            }
        });


        // Set the text for the TextView
        tvItem.setText(item);
        tvPrice.setText("$" + price.toString());
        tvNames.setText(names.toString());



        return convertView;
    }

    private void openEditItemDialog() {
    }

    public void setRecyclerViewAdapter(RecyclerViewAdapter nameListAdapter){

        this.nameListAdapter = nameListAdapter;
    }

}