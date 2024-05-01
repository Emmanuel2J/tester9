package com.example.tester;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ModelAdepter extends FirebaseRecyclerAdapter<MainModel,ModelAdepter.ViewHolder> {
    public ModelAdepter(@NonNull FirebaseRecyclerOptions<MainModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ModelAdepter.ViewHolder viewHolder, int i, @NonNull MainModel mainModel) {

        viewHolder.tvName.setText(mainModel.getName());
        viewHolder.tvPosition.setText(mainModel.getPosition());
        viewHolder.tvEmail.setText(mainModel.getEmail());

        Picasso.get().load(mainModel.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(viewHolder.imageView);

    }

    @NonNull
    @Override
    public ModelAdepter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false));
    }




    public static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageView;
        TextView tvName,tvPosition,tvEmail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.imageView);
            tvName=itemView.findViewById(R.id.tvName);
            tvPosition=itemView.findViewById(R.id.tvPosition);
            tvEmail=itemView.findViewById(R.id.tvEmail);


        }

    }
}
