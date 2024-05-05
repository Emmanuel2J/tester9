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

public abstract class ModelAdepter_service extends FirebaseRecyclerAdapter<MainModel, ModelAdepter_service.ViewHolder> {
    private OnItemClickListener listener;

    public abstract void onItemClick(MainModel_service mainModel);

    public interface OnItemClickListener {
        void onItemClick(MainModel mainModel);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ModelAdepter_service(@NonNull FirebaseRecyclerOptions<MainModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull MainModel mainModel) {
        viewHolder.tvName.setText(mainModel.getName());
        viewHolder.tvPosition.setText(mainModel.getPosition());
        viewHolder.tvEmail.setText(mainModel.getEmail());

        Picasso.get().load(mainModel.getImage())
                .placeholder(R.drawable.ic_launcher_background)
                .into(viewHolder.imageView);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(mainModel);
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPosition, tvEmail;
        CircleImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            imageView = itemView.findViewById(R.id.imageView1);
        }
    }
}
