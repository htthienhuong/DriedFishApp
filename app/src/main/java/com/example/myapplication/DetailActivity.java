package com.example.myapplication;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private RecyclerView rcvRecipe;
    private RecipeAdapter mRecipeAdapter;
    private List<Recipe> mListRecipe;

    Fish_Item fish_item;
    Bundle bundle;

    // Reference to database of all recipes
    // of the fish of this detail activity
    public static DatabaseReference recipeReference;

    public final ValueEventListener onRecipeChanged = new ValueEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot data : snapshot.getChildren())
                mListRecipe.add(data.getValue(Recipe.class));
            mRecipeAdapter.notifyDataSetChanged();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipe);

        bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        fish_item = (Fish_Item) bundle.get("object_fish");


        ImageView imgFish = findViewById(R.id.ivFish);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvName_en = findViewById(R.id.tvName_En);
        ImageButton back = findViewById(R.id.back_pressed);

        rcvRecipe = findViewById(R.id.list_recipe);

        imgFish.setImageBitmap (
                BitmapFactory.decodeResource (
                        imgFish.getContext().getResources(),
                        fish_item.getImage()
                )
        );
        tvName.setText(fish_item.getName());
        tvPrice.setText(fish_item.getPrice());
        tvName_en.setText(fish_item.getName_en());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvRecipe.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        rcvRecipe.addItemDecoration(dividerItemDecoration);

        mListRecipe = new ArrayList<>();
        mRecipeAdapter = new RecipeAdapter(DetailActivity.this, mListRecipe);
        rcvRecipe.setAdapter(mRecipeAdapter);

        if (LocaleHelper.getLanguage(DetailActivity.this).equals("vi")) {
            recipeReference = FirebaseDatabase.getInstance()
                    .getReference("Recipes-vietnamese")
                    .child(fish_item.getClassLabel());
        } else {
            recipeReference = FirebaseDatabase.getInstance()
                    .getReference("Recipes-english")
                    .child(fish_item.getClassLabel());
        }
        recipeReference.addValueEventListener(onRecipeChanged);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
