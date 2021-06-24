package app.birdsoft.painelmeurestaurante.tools;

import android.content.DialogInterface;

public final class DialogComplement implements DialogInterface.OnDismissListener {
    public static final DialogComplement INSTANCE = new DialogComplement();

    private /* synthetic */ DialogComplement() {
    }

    public final void onDismiss(DialogInterface dialogInterface) {
     //   EventBus.getDefault().post(new DismissDialogEvent());
    }
}
