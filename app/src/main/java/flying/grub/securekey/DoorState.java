package flying.grub.securekey;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by fly on 03/03/15.
 */
public class DoorState extends View {

    private int width;
    private int height;
    private final Rect textBounds;

    private static int DEFAULT_SIZE_FULL;
    private static int DEFAULT_SIZE_SMALL;
    private int curSize;

    private boolean shouldAnimate = false;
    private boolean open = false;
    private boolean connected = false;

    public DoorState(Context context, AttributeSet attrs) {
        super(context, attrs);
        textBounds = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        DEFAULT_SIZE_SMALL = height-50;
        DEFAULT_SIZE_FULL = height;
        curSize = height;
        setMeasuredDimension(width, height);
    }

    public void open(){
        shouldAnimate = true;
        open = true;
        invalidate();

    }

    public void close(){
        shouldAnimate = true;
        open = false;
        invalidate();

    }

    public void setConnected(boolean b){
        shouldAnimate = true;
        this.connected = b;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getnextSize();
        //Log.d("test", shouldAnimate + " " + open + curSize);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);

        String s;
        if (!connected){
            s = "Disconnected";
            canvas.drawRect(width/2 - curSize/2, height/2 - curSize/2, width/2 + curSize/2, height/2 + curSize/2, paint);
        }else if (open){
            s = "Open";
            canvas.drawCircle(width/2, height/2, curSize/2, paint);
        }else{
            canvas.drawCircle(width / 2, height/2, curSize/2, paint);
            s = "Closed";
        }


        Typeface tf = Typeface.create("Roboto",Typeface.NORMAL);
        paint.setTypeface(tf);
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);

        paint.getTextBounds(s, 0, s.length(), textBounds);
        canvas.drawText(s, width / 2 - textBounds.exactCenterX(), height / 2 - textBounds.exactCenterY(), paint);
    }

    public void getnextSize(){
        if (shouldAnimate){
            if(open){
                if(curSize > DEFAULT_SIZE_SMALL){
                    curSize -=10;
                    invalidate();
                }else{
                    shouldAnimate = false;
                }
            }else{
                if(curSize < DEFAULT_SIZE_FULL){
                    curSize +=10;
                    invalidate();
                }else{
                    shouldAnimate = false;
                }
            }
        }
    }

}
