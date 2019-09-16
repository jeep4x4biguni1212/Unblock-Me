package com.unblockme.unblockme.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.unblockme.unblockme.R;
import com.unblockme.unblockme.core.Block;
import com.unblockme.unblockme.core.Grid;
import com.unblockme.unblockme.utils.Bound;
import com.unblockme.unblockme.utils.Orientation;
import com.unblockme.unblockme.utils.Position;

import java.math.RoundingMode;
import java.text.DecimalFormat;


public class GridView extends SurfaceView implements SurfaceHolder.Callback {
    private static final int H2 = 1;
    private static final int H3 = 2;
    private static final int V2 = 3;
    private static final int V3 = 4;
    private final SurfaceHolder holder;
    private final int gPadding = 10;
    private Grid grid;
    private ArrayMap<Integer, Bitmap> tiles = new ArrayMap<>();
    private RectF outline;
    private Canvas mCanvas;
    private RectF background;
    private float bWidth;
    private float gWidth;
    private Bitmap bitmap;
    private int current_id;
    private float xOnDown, yOnDown, fLeft, fTop;
    private Bound bound;
    private boolean dragged = false;

    @SuppressLint("ClickableViewAccessibility")
    public GridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.holder = this.getHolder();
        this.holder.addCallback(this);
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("GridView", "SurfaceCreated");
        mCanvas = this.holder.lockCanvas();
        if (mCanvas == null)
            return;
        this.bitmap = Bitmap.createBitmap(
                mCanvas.getWidth(), mCanvas.getHeight(), Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(this.bitmap);

        gWidth = this.holder.getSurfaceFrame().width();
        Log.i("GridView", "gWidth: " + gWidth);
        bWidth = (gWidth - (2 * gPadding)) / Grid.GRID_SIZE;
        Log.i("GridView", "bWidth: " + this.bWidth);


        outline = new RectF(this.holder.getSurfaceFrame().left, this.holder.getSurfaceFrame().top,
                this.holder.getSurfaceFrame().left + gWidth, this.holder.getSurfaceFrame().top + gWidth
        );

        background = new RectF(outline.left + gPadding, outline.top + gPadding,
                outline.right - gPadding, outline.bottom - gPadding);

        this.drawGrid(canvas);

        bWidth = (gWidth - (2 * gPadding)) / Grid.GRID_SIZE;

        //{{ load and scale block bitmaps
        Bitmap thumb = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.m),
                (int) (bWidth * 2),
                (int) (bWidth * 1), false);

        this.tiles.put(Grid.MARKED_ID, thumb);

        thumb = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.h2),
                (int) (bWidth * 2),
                (int) (bWidth * 1), false);

        this.tiles.put(H2, thumb);

        thumb = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.h3),
                (int) (bWidth * 3),
                (int) (bWidth * 1), false);

        this.tiles.put(H3, thumb);

        thumb = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.v2),
                (int) (bWidth * 1),
                (int) (bWidth * 2), false);

        this.tiles.put(V2, thumb);

        thumb = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.v3),
                (int) (bWidth * 1),
                (int) (bWidth * 3), false);

        this.tiles.put(V3, thumb);
        //}}


        this.drawBlocks(canvas);

        mCanvas.drawBitmap(this.bitmap, 0, 0, null);
        this.holder.unlockCanvasAndPost(mCanvas);
    }

    /**
     * Defini la taille de la vue.
     * La vue prend automatiquement la largeur de son parent comme longueur et largeur.
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(parentViewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(parentViewWidth, MeasureSpec.EXACTLY));
    }

    /**
     * Dessine les bords et le fond de la grille
     * @param canvas
     *
     */
    private void drawGrid(Canvas canvas) {

        canvas.drawColor(getResources().getColor(R.color.colorBackground));

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.colorEdge));
        paint.setStrokeWidth(getResources().getInteger(R.integer.lineThickness));

        canvas.drawLine(outline.left, outline.top, outline.right, outline.top, paint);
        canvas.drawLine(outline.right, outline.top + gPadding, outline.right, outline.top + gPadding + 2 * this.bWidth, paint);
        canvas.drawLine(outline.right, outline.top + gPadding + 3 * this.bWidth, outline.right, outline.bottom, paint);
        canvas.drawLine(outline.left, outline.bottom, outline.right, outline.bottom, paint);
        canvas.drawLine(outline.left, outline.top, outline.left, outline.bottom, paint);


    }

    /**
     * Dessine les blocs a l'interieure de la grille
     *  @param canvas
     *
     */
    private void drawBlocks(Canvas canvas) {
        for (Integer id : grid.getBlockIds()) {
            this.drawBlock(id, canvas);
        }
    }

    private void drawBlock(int id, Canvas canvas) {
        Block block = grid.getBlockById(id);

        float left, top;
        left = this.background.left + bWidth * block.getPosition().getX();
        top = this.background.top + bWidth * block.getPosition().getY();

        Bitmap thumb;

        if (block.getOrientation() == Orientation.HORIZONTAL) {
            thumb = (block.getDimension().getWidth() == 2) ? this.tiles.get(H2) : this.tiles.get(H3);
        } else {
            thumb = (block.getDimension().getLength() == 2) ? this.tiles.get(V2) : this.tiles.get(V3);
        }

        if (id == Grid.MARKED_ID)
            thumb = this.tiles.get(Grid.MARKED_ID);

        canvas.drawBitmap(thumb, left, top, null);
        invalidate();
    }

    private Bitmap drawOverlay(int bid) {

        mCanvas = this.holder.lockCanvas();
        this.bitmap = Bitmap.createBitmap(mCanvas.getWidth(),
                mCanvas.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(this.bitmap);

        this.drawGrid(canvas);

        for (Integer id : grid.getBlockIds()) {
            if (bid == id) continue;
            this.drawBlock(id, canvas);
        }
        Bitmap overlayBitmap = Bitmap.createBitmap(this.bitmap);

        canvas.setBitmap(overlayBitmap);
        this.drawBlock(bid, canvas);

        mCanvas.drawBitmap(overlayBitmap, 0, 0, null);
        this.holder.unlockCanvasAndPost(mCanvas);
        return bitmap;
    }

    private void removeOverlay() {
        mCanvas = this.holder.lockCanvas();
        mCanvas.setBitmap(this.bitmap);
        mCanvas.drawBitmap(this.bitmap, 0, 0, null);
        this.holder.unlockCanvasAndPost(mCanvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int left, top;

        Bitmap map;
        Block block;

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("TEvt", "down");

                left = (int) Math.floor(e.getX() / bWidth);
                top = (int) Math.floor(e.getY() / bWidth);

                this.current_id = grid.getIdByPosition(new Position(left, top));
                if (this.current_id == 0) break;
                this.xOnDown = e.getX();
                this.yOnDown = e.getY();
                this.bound = grid.getMoveBoundaries(this.current_id);

                drawOverlay(this.current_id);
                Log.i("BOUND", this.bound.toString());
                break;

            case MotionEvent.ACTION_MOVE:

                block = grid.getBlockById(this.current_id);
                if (block == null) break;
                Position p = block.getPosition();

                // get the new left and top

                fLeft = p.getX() * bWidth + this.background.left;
                fTop = p.getY() * bWidth + this.background.top;

                Log.i("MOVE", "1- fLeft: " + fLeft + ";\t\tfTop:" + fTop);

                if (block.getOrientation() == Orientation.HORIZONTAL) {
                    float diff = e.getX() - this.xOnDown;
                    if (Math.abs(e.getY() - this.yOnDown) > bWidth) break;

                    fLeft += diff;
                    if (fLeft > this.bound.getHigh() * bWidth + this.background.left)
                        fLeft = this.bound.getHigh() * bWidth + this.background.left;

                    if (fLeft < this.bound.getLow() * bWidth + this.background.left)
                        fLeft = this.bound.getLow() * bWidth + this.background.left;

                } else {
                    float diff = e.getY() - this.yOnDown;
                    if (Math.abs(e.getX() - this.xOnDown) > bWidth) break;
                    fTop += diff;

                    if (fTop > this.bound.getHigh() * bWidth + this.background.top)
                        fTop = this.bound.getHigh() * bWidth + this.background.top;

                    if (fTop < this.bound.getLow() * bWidth + this.background.top)
                        fTop = this.bound.getLow() * bWidth + this.background.top;
                }


                Log.i("MOVE", "2- fLeft: " + fLeft + ";\t\tfTop:" + fTop);

                map = Bitmap.createBitmap(this.bitmap);
                Canvas canvas = new Canvas(map);

                //{{ DRAW SLIDING BLOCK
//                Paint paint = new Paint();
//                paint.setColor(Color.rgb(100, 100, 255));
//
//                canvas.drawRect(fLeft, fTop, fLeft + block.getDimension().getWidth() * bWidth,
//                        fTop + block.getDimension().getLength() * bWidth, paint);

                Bitmap thumb;

                if (block.getOrientation() == Orientation.HORIZONTAL) {
                    thumb = (block.getDimension().getWidth() == 2) ? this.tiles.get(H2) : this.tiles.get(H3);
                } else {
                    thumb = (block.getDimension().getLength() == 2) ? this.tiles.get(V2) : this.tiles.get(V3);
                }

                if (this.current_id == Grid.MARKED_ID)
                    thumb = this.tiles.get(Grid.MARKED_ID);

                canvas.drawBitmap(thumb, fLeft, fTop, null);
                invalidate();
                //}}
// draw current_block over overlay
                mCanvas = this.holder.lockCanvas();
                mCanvas.drawBitmap(map, 0, 0, null);
                this.holder.unlockCanvasAndPost(mCanvas);
                this.dragged = true;
                break;

            case MotionEvent.ACTION_UP:
                Log.i("TEvt", "up");
                if ((this.current_id == 0) || (!this.dragged)) break;
                removeOverlay(); // grid avec le bloc efface

                DecimalFormat df = new DecimalFormat("#");
                df.setRoundingMode(RoundingMode.HALF_UP);

                left = Integer.parseInt(df.format(fLeft / bWidth));
                top = Integer.parseInt(df.format(fTop / bWidth));
                Log.i("AUP", "left:" + left + ";\t\ttop:" + top);
                if (grid.move(this.current_id, new Position(left, top))) { // ALWAYS TRUE
                    // other stuff here
                    Log.i("AUP", "Moved");
                }
//                // draw the missing block or draw the entire grid
                map = Bitmap.createBitmap(this.bitmap);
                canvas = new Canvas(map);
                this.drawBlock(this.current_id, canvas);
                mCanvas = this.holder.lockCanvas();
                mCanvas.drawBitmap(map, 0, 0, null);
                this.holder.unlockCanvasAndPost(this.mCanvas);
                this.dragged = false;
                break;
        }

        return true;
    }

    public void update() {

        mCanvas = this.holder.lockCanvas();
        this.bitmap = Bitmap.createBitmap(
                mCanvas.getWidth(), mCanvas.getHeight(), Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(this.bitmap);

        this.drawGrid(canvas);
        this.drawBlocks(canvas);

        mCanvas.drawBitmap(this.bitmap, 0, 0, null);
        this.holder.unlockCanvasAndPost(mCanvas);
    }
}
