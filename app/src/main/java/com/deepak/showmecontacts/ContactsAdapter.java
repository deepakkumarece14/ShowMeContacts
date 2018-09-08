package com.deepak.showmecontacts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {
    private List<MyContacts> myContacts;
    private Context context;

    ContactsAdapter(List<MyContacts> myContacts,Context context) {
        this.myContacts = myContacts;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.contact_item_layout,viewGroup,false);
        return new ContactsViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        MyContacts contacts = myContacts.get(position);
        holder.displayName.setText(contacts.getContactName());
        holder.phoneNumber.setText(contacts.getContactNumber().get(0));
        holder.displayImage.setImageBitmap(contacts.getContactImage());
    }

    @Override
    public int getItemCount() {
        return myContacts.size();
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView displayName;
        private TextView phoneNumber;
        private ImageView displayImage;
        private Context context;

        ContactsViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);
            this.context = context;
            this.displayName = itemView.findViewById(R.id.contact_name);
            this.phoneNumber = itemView.findViewById(R.id.contact_mobile);
            this.displayImage = itemView.findViewById(R.id.contact_image);
            itemView.setOnClickListener(this);
            this.displayImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "OnImageClick...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "OnClick...", Toast.LENGTH_SHORT).show();
        }
    }
}
