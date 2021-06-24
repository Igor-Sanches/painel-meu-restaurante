package app.birdsoft.painelmeurestaurante.manager;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper.Callback;

public class DragItemTouchHelper extends Callback {
    private final MoveHelperAdapter mAdapter;

    public interface MoveHelperAdapter {
        void onItemMove(int i, int i2);
    }

    public interface TouchViewHolder {
        void onItemClear();

        void onItemSelected();
    }

    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    public boolean isLongPressDragEnabled() {
        return true;
    }

    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
    }

    public DragItemTouchHelper(MoveHelperAdapter moveHelperAdapter) {
        this.mAdapter = moveHelperAdapter;
    }

    public int getMovementFlags(RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            return makeMovementFlags(15, 0);
        }
        return makeMovementFlags(3, 48);
    }

    public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
        if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
            return false;
        }
        this.mAdapter.onItemMove(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
        return true;
    }

    public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
        if (i == 1) {
            viewHolder.itemView.setAlpha(1.0f - (Math.abs(f) / ((float) viewHolder.itemView.getWidth())));
            viewHolder.itemView.setTranslationX(f);
            return;
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
}
