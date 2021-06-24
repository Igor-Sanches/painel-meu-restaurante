package app.birdsoft.painelmeurestaurante.manager;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;

public class SwipeItemTouchHelper extends Callback {
    private final SwipeHelperAdapter mAdapter;

    public interface SwipeHelperAdapter {
        void onItemDismiss(int i);
    }

    public interface TouchViewHolder {
        void onItemClear();

        void onItemSelected();
    }

    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    public boolean isLongPressDragEnabled() {
        return false;
    }

    public SwipeItemTouchHelper(SwipeHelperAdapter swipeHelperAdapter) {
        this.mAdapter = swipeHelperAdapter;
    }

    public int getMovementFlags(RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            return makeMovementFlags(15, 0);
        }
        return makeMovementFlags(3, 48);
    }

    public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
        return viewHolder.getItemViewType() == viewHolder2.getItemViewType();
    }

    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        this.mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
        if (i == 1) {
            View view = viewHolder.itemView;
            ColorDrawable colorDrawable = new ColorDrawable();
            colorDrawable.setColor(getBgColorCode());
            if (f > 0.0f) {
                colorDrawable.setBounds(view.getLeft(), view.getTop(), (int) f, view.getBottom());
            } else {
                colorDrawable.setBounds(view.getRight() + ((int) f), view.getTop(), view.getRight(), view.getBottom());
            }
            colorDrawable.draw(canvas);
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
    }

    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
        if (i != 0 && (viewHolder instanceof TouchViewHolder)) {
            ((TouchViewHolder) viewHolder).onItemSelected();
        }
        super.onSelectedChanged(viewHolder, i);
    }

    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setAlpha(1.0f);
        if (viewHolder instanceof TouchViewHolder) {
            ((TouchViewHolder) viewHolder).onItemClear();
        }
    }

    public int getBgColorCode() {
        return -3355444;
    }

}
