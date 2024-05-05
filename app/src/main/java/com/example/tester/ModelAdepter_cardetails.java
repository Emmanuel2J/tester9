package com.example.tester;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class ModelAdepter_cardetails extends FirebaseRecyclerAdapter<MainModel, ModelAdepter_cardetails.ViewHolder> {

    public abstract void onItemClick(MainModel mainModel);

    public interface OnItemClickListener {
        void onItemClick(MainModel mainModel);
    }

    private final OnItemClickListener listener;

    public ModelAdepter_cardetails(@NonNull FirebaseRecyclerOptions<MainModel> options, OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull MainModel mainModel) {
        viewHolder.bind(mainModel, listener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView tvName, tvPosition, tvEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView1);
            tvName = itemView.findViewById(R.id.tvName);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            tvEmail = itemView.findViewById(R.id.tvEmail);
        }

        public void bind(final MainModel mainModel, final OnItemClickListener listener) {
            tvName.setText(mainModel.getName());
            tvPosition.setText(mainModel.getPosition());
            tvEmail.setText(mainModel.getEmail());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(mainModel);
                }
            });
        }
    }
}
