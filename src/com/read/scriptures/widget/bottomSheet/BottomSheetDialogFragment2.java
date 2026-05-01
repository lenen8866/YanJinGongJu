package com.read.scriptures.widget.bottomSheet;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatDialogFragment;
/**
 * Time: 2020/8/28
 * Author: a123
 * Description:
 */

/**
 * Modal bottom sheet. This is a version of {@link DialogFragment} that shows a bottom sheet
 * using {@link BottomSheetDialog2} instead of a floating dialog.
 */
public class BottomSheetDialogFragment2 extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog2(getContext(), getTheme());
    }

}