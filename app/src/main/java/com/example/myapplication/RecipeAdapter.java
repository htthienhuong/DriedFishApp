package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>{

    public RecipeAdapter(Context context, List<Recipe> mListRecipe) {
        this.mListRecipe = mListRecipe;
        _context = context;
    }

    private List<Recipe> mListRecipe;
    private Context _context;

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe,parent,false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = mListRecipe.get(position);
        if (recipe == null) return;
        holder.recipeName.setText(recipe.getNameRecipe());
        holder.recipeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent moreDetailIntent = new Intent(_context, MoreDetailActivity.class);
                moreDetailIntent.putExtra("recipe", recipe);
                _context.startActivity(moreDetailIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mListRecipe != null){
            return mListRecipe.size();
        }
        return 0;
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder{
        private TextView recipeName;
        private CardView recipeCardView;
        private CircleImageView recipeImageView;

        public RecipeViewHolder(@NonNull View itemView){
            super(itemView);
            recipeName = itemView.findViewById(R.id.name_recipe);
            recipeCardView = itemView.findViewById(R.id.card_recipe);
            recipeImageView = itemView.findViewById(R.id.img_recipe);
        }
    }
}
