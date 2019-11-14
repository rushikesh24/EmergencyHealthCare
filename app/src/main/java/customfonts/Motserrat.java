package customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class Motserrat extends android.support.v7.widget.AppCompatTextView {

    public Motserrat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Motserrat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Motserrat(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Montserrat-Bold.otf");
            setTypeface(tf);
        }
    }

}