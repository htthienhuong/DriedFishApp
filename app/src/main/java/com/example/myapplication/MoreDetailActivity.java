package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MoreDetailActivity extends AppCompatActivity {

    final String TAG = "MoreDetailActivity";

    private CircleImageView recipeImageView;
    private TextView recipeNameTextView, ingredientsTextView, instructionTextView;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_detail);

        // Get the recipe from intent
        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        if (recipe == null)
            Log.d(TAG, "Can't get recipe from intent");

        // Bind UI Components
        recipeImageView = findViewById(R.id.more_detail_recipe_image);
        recipeNameTextView = findViewById(R.id.more_detail_recipe_name);
        ingredientsTextView = findViewById(R.id.more_detail_ingredients_content);
        instructionTextView = findViewById(R.id.more_detail_instruction_content);
        backButton = findViewById(R.id.more_detail_back_button);

        // Make the text view scrollable
        ingredientsTextView.setMovementMethod(new ScrollingMovementMethod());
        instructionTextView.setMovementMethod(new ScrollingMovementMethod());

        // Set content for UI components

        // Set up Recipe Image
        String fishLabel = DetailActivity.recipeReference.getKey();
        Fish_Item fish = Fish_Item.getFishByLabel(fishLabel);

        if (fish != null) recipeImageView.setImageResource(fish.getImage());
        else Log.d(TAG, "Can't not find the fish");

        // Set up text views content
        if (recipe != null) {
            recipeNameTextView.setText(recipe.getNameRecipe());
            ingredientsTextView.setText(recipe.getIngredients());
            instructionTextView.setText(recipe.getInstructions());
        }

        // Set up action of back button pressed
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}