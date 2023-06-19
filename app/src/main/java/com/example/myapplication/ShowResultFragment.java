package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.util.Stack;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowResultFragment extends Fragment {
    Bitmap selectedImage;
    Fish_Item predictedFish;
    String confidence;

    View view;

    ImageView selectedImageView;
    CircleImageView predictedFishImageView;
    TextView predictedFishName;
    TextView predictedFishPrice;
    TextView predictedFishNameEn;
//    TextView confidenceTextView;
    CardView predictedFishCardView;

    Stack<Bitmap> imageStack = new Stack<>();
    Stack<Pair<Fish_Item, String>> resultStack = new Stack<>();

    public ShowResultFragment() {
        super(R.layout.activity_show_result);
    }

    public ShowResultFragment(Bitmap selectedImage, Fish_Item predictedResult, String confidence) {
        super(R.layout.activity_show_result);
        this.selectedImage = selectedImage;
        this.predictedFish = predictedResult;
        this.confidence = confidence;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            selectedImageView = view.findViewById(R.id.selected_image_view);
            predictedFishImageView = view.findViewById(R.id.predicted_image);
            predictedFishName = view.findViewById(R.id.name_fish);
            predictedFishPrice = view.findViewById(R.id.price_fish);
            predictedFishNameEn = view.findViewById(R.id.name_en_fish_re);
//            confidenceTextView = view.findViewById(R.id.confidence_TextView);
            predictedFishCardView = view.findViewById(R.id.show_result_card_view);

            selectedImageView.setImageBitmap(this.selectedImage);

            if (predictedFish != null) {
                predictedFishImageView.setImageBitmap (
                        BitmapFactory.decodeResource (
                                view.getContext().getResources(),
                                predictedFish.getImage()
                        )
                );
                predictedFishName.setText(predictedFish.getName());
                predictedFishPrice.setText(predictedFish.getPrice());
                predictedFishNameEn.setText(predictedFish.getName_en());
//                confidenceTextView.setText(confidence);

                if(!predictedFish.isLabel("none")){
                predictedFishCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FishAdapter.onClickGoToDetail(getContext(), predictedFish);
                    }
                });}
            }
        } else
            Log.d("ShowResultFragment", "onCreateView: view is null");

        return view;
    }
}
