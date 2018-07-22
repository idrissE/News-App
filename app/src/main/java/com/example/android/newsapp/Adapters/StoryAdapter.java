package com.example.android.newsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.newsapp.Models.Contributor;
import com.example.android.newsapp.Models.Story;
import com.example.android.newsapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    private List<Story> mStories;
    private Context mContext;

    public StoryAdapter(List<Story> stories, Context context) {
        mStories = stories;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View storyView = LayoutInflater.from(mContext)
                .inflate(R.layout.story_list_item, parent, false);

        return new ViewHolder(storyView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mStories.size();
    }

    /*
     * Unlike a subclass of ArrayAdapter, a subclass of RecyclerView.Adapter
     * doesn't have a clear or addAll methods that's why custom implementations
     * similar to ArrayAdapter's ones were created.
     */
    public void clear() {
        mStories.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Story> stories) {
        mStories.addAll(stories);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            Story currentStory = mStories.get(position);

            // setup the title
            TextView storyTitleTv = itemView.findViewById(R.id.story_title);
            storyTitleTv.setText(currentStory.getTitle());
            // setup the section name
            TextView storySectionTv = itemView.findViewById(R.id.story_section);
            storySectionTv.setText(currentStory.getSectionName());
            // setup the date
            TextView storyDateTv = itemView.findViewById(R.id.story_date);
            Date currentDate = currentStory.getDate();
            if (currentDate != null) {
                String dateTxt = formatDate(currentDate);
                storyDateTv.setText(dateTxt);
            } else {
                storyDateTv.setVisibility(View.GONE);
            }
            // setup the contributors
            TextView contributorsTv = itemView.findViewById(R.id.story_contributors);
            List<Contributor> contributors = currentStory.getContributors();
            if (contributors != null)
                contributorsTv.setText(getContributorsSummary(contributors));
            else
                contributorsTv.setVisibility(View.GONE);
            // setup thumbnail
            ImageView thumbnailImg = itemView.findViewById(R.id.story_thumbnail);
            Bitmap thumbnailBitmap = currentStory.getThumbnail();
            if (thumbnailBitmap != null)
                thumbnailImg.setImageBitmap(currentStory.getThumbnail());
            else
                thumbnailImg.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {
            int selectedItem = getAdapterPosition();
            Story selectedStory = mStories.get(selectedItem);
            Uri pageUri = Uri.parse(selectedStory.getUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, pageUri);
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                mContext.startActivity(intent);
            }
        }

        private String formatDate(Date dateObject) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return dateFormat.format(dateObject);
        }

        /**
         * Transforms the list of contributors into a string with
         * each contributor in a line
         *
         * @param contributors list of article contributors
         * @return summary of contributors
         */
        private String getContributorsSummary(List<Contributor> contributors) {
            StringBuilder summary = new StringBuilder();
            for (Contributor contributor : contributors) {
                summary.append(contributor.toString());
                summary.append("\n");
            }

            return summary.toString();
        }
    }

    /**
     * RecyclerView doesn't come with a divider by default that's why
     * a custom divider should be created.
     * Code inspired from: https://gist.github.com/polbins/e37206fbc444207c0e92
     * Instead of creating a separate class, it seemed to be more organized to put it as
     * a static inner class
     */
    public static class StoryItemsDivider extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public StoryItemsDivider(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.recycler_item_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
