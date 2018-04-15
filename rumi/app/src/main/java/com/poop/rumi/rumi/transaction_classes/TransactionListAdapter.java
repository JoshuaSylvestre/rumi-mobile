package com.poop.rumi.rumi.transaction_classes;


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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

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

    private ArrayList<Transaction> transactionList;
    private RecyclerViewAdapter nameListAdapter;

    ViewGroup parent;

    public TransactionListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Transaction> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        transactionList =  objects;
    }


    public void setRecyclerViewAdapter(RecyclerViewAdapter nameListAdapter){

        this.nameListAdapter = nameListAdapter;
    }

    public static class ViewHolder{


        TextView itemTextView;
        TextView priceTextView;
        TextView namesTextView;

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

        this.parent = parent;
        View rowView = convertView;

        // Get transaction information
        final String item = getItem(position).getItem();
        final ArrayList<String> names = getItem(position).getNames();
        final Float price = getItem(position).getPrice();


        if(convertView == null){

            LayoutInflater inflater = LayoutInflater.from(mContext);
            rowView = inflater.inflate(mResource, parent, false);

            ViewHolder vh = new ViewHolder();
            vh.itemTextView = rowView.findViewById(R.id.itemView);
            vh.priceTextView = rowView.findViewById(R.id.priceView);
            vh.namesTextView = rowView.findViewById(R.id.namesView);

            rowView.setTag(vh);

        }


        final ViewHolder holder = (ViewHolder) rowView.getTag();

//        // Create layoutinflatter, take convertView from the getView
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        convertView = inflater.inflate(mResource, parent, false);
//
//        // Declare TextView objects:
//        // In Main: don't need to call the View, but in here yes: convertView
//        final TextView tvItem = (TextView) convertView.findViewById(R.id.textView1);
//        final TextView tvPrice = (TextView) convertView.findViewById(R.id.textView3);
//        final TextView tvNames = (TextView) convertView.findViewById(R.id.textView2);

        final LinearLayout linearLayout = (LinearLayout)rowView.findViewById(R.id.parent_layout_item_price);

        final View finalRowView = rowView;
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(mContext, transactionList.get(position).getItem()+", "+transactionList.get(position).getPrice() , Toast.LENGTH_SHORT).show();

                if(nameListAdapter.getLastNamePos() != -1) {

                    //Toast.makeText(mContext, "Tryna add name: " + nameListAdapter.getLastNameTapped(), Toast.LENGTH_SHORT).show();

                    if (names.contains(nameListAdapter.getLastNameTapped())) {

                        transactionList.get(position).removeName(nameListAdapter.getLastNameTapped());
                        finalRowView.setBackgroundColor(Color.rgb(238,238,255));

                        if(names.size() <= 5)
                            holder.namesTextView.setText(names.toString());
                        else if(names.size() > 5)
                            holder.namesTextView.setText(names.size()+" people shared this");

                        // The colors aren't working perfectly because they disappeared after scrolling up or down.
                        // comment out for now, will come back later:
//                        tvItem.setBackgroundColor(Color.rgb(153,204,255));
//                        tvNames.setBackgroundColor(Color.rgb(153,204,255));
//                        tvPrice.setBackgroundColor(Color.rgb(153,204,255));

                    }
                    else{

                        transactionList.get(position).addName(nameListAdapter.getLastNameTapped());
                        finalRowView.setBackgroundColor(Color.rgb(87,188,150));

                        if(names.size() <= 5)
                            holder.namesTextView.setText(names.toString());
                        else if(names.size() > 5)
                            holder.namesTextView.setText(names.size()+" people shared this");

//                        tvItem.setBackgroundColor(Color.rgb(87,188,150));
//                        tvNames.setBackgroundColor(Color.rgb(87,188,150));
//                        tvPrice.setBackgroundColor(Color.rgb(87,188,150));
                    }



                }

            }
        });

        linearLayout.isLongClickable();
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                LayoutInflater inflater = LayoutInflater.from(mContext);

                final View dialogView = inflater.inflate(R.layout.dialog_add_or_edit_item,null);

                builder.setView(dialogView);


                final EditText editText_item_name = (EditText) dialogView.findViewById(R.id.edit_item_name);
                final EditText editText_item_price = (EditText) dialogView.findViewById(R.id.edit_item_price);

                editText_item_name.setSelection(editText_item_name.getText().length());

                editText_item_name.setText(item);
                editText_item_price.setText(price.toString());

                Button three_dot_btn = (Button) dialogView.findViewById(R.id.dialog_positive_btn);

                // Create the alert dialog
                final AlertDialog dialog = builder.create();

                // Set the positive button
                three_dot_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        System.out.println("Item Name: "+editText_item_name.getText().toString());
                        System.out.println("Price: "+editText_item_price.getText().toString());

                        transactionList.get(position).setItem(editText_item_name.getText().toString());
                        transactionList.get(position).setPrice(Float.parseFloat(editText_item_price.getText().toString()));

                        holder.itemTextView.setText(editText_item_name.getText().toString());
                        holder.priceTextView.setText("$" + editText_item_price.getText().toString());

                        // tryna fix the fact that on re-edit, orig items name appear
//                        editText_item_name.setText(tvItem.getText().toString());
//                        editText_item_price.setText(tvPrice.getText().toString());

                        dialog.dismiss();
                    }
                });


                // Finally, display the alert dialog
                dialog.show();

                return true;

            }
        });

//        Button dotsButton = (Button) rowView.findViewById(R.id.threeDotsButton);
//        dotsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                // TODO: create function for this instead
//                // openEditItemDialog();
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//
//                LayoutInflater inflater = LayoutInflater.from(mContext);
//
//                final View dialogView = inflater.inflate(R.layout.dialog_add_or_edit_item,null);
//
//                builder.setView(dialogView);
//
//
//                final EditText editText_item_name = (EditText) dialogView.findViewById(R.id.edit_item_name);
//                final EditText editText_item_price = (EditText) dialogView.findViewById(R.id.edit_item_price);
//
//                editText_item_name.setSelection(editText_item_name.getText().length());
//
//                editText_item_name.setText(item);
//                editText_item_price.setText(price.toString());
//
//                Button three_dot_btn = (Button) dialogView.findViewById(R.id.dialog_positive_btn);
//
//                // Create the alert dialog
//                final AlertDialog dialog = builder.create();
//
//                // Set the positive button
//                three_dot_btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        System.out.println("Item Name: "+editText_item_name.getText().toString());
//                        System.out.println("Price: "+editText_item_price.getText().toString());
//
//                        transactionList.get(position).setItem(editText_item_name.getText().toString());
//                        transactionList.get(position).setPrice(Float.parseFloat(editText_item_price.getText().toString()));
//
//                        holder.itemTextView.setText(editText_item_name.getText().toString());
//                        holder.priceTextView.setText("$" + editText_item_price.getText().toString());
//
//                        // tryna fix the fact that on re-edit, orig items name appear
////                        editText_item_name.setText(tvItem.getText().toString());
////                        editText_item_price.setText(tvPrice.getText().toString());
//
//                        dialog.dismiss();
//                    }
//                });
//
//
//                // Finally, display the alert dialog
//                dialog.show();
//
//            }
//        });


        // Set the text for the TextView
        holder.itemTextView.setText(item);
        holder.priceTextView.setText("$" + price.toString());
        holder.namesTextView.setText(names.toString());


        return rowView;
    }

    public void highlightSelectedItems(String name){


        ListView lv = parent.findViewById(R.id.vertical_list_item_price_name);
        View v;

        for(int i = 0; i < transactionList.size(); i++){

            if(transactionList.get(i).getNames().contains(name)){

                v = getViewByPosition(i, lv);

                v.setBackgroundColor(Color.rgb(87,188,150));

            }

        }




    }

    public void undoHighlightedItems(String name){




    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }



}