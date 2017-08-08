package cs371m.alarming;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by nano on 8/7/17.
 */

public class FallingShapesBoard extends View {
    int mRows = 10; // for y
    int mColumns = 10; // for x
    FallingShapes mFallingShapes;
    private String LOG_TAG = "FallingShapesBoard";
    public FallingShapesBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFallingShapes = new FallingShapes(10, 50);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Determine the width and height of the View
        int boardWidth = getWidth();
        int boardHeight = getHeight();

        drawGrid(canvas, boardWidth, boardHeight);
        drawFallingShapes(canvas, boardWidth, boardHeight);
    }

    @Override
    public void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(size, size);
    }

    public void drawGrid(Canvas canvas, int boardWidth, int boardHeight) {
        Rect drawingRect = new Rect();
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        int cellHeight = boardHeight / mRows;
        int cellWidth = boardWidth / mColumns;
        for (int i = 0; i < mRows; ++i) {
            float x1 = 0;
            float y1 = (cellHeight * i);
            float x2 = boardWidth;
            float y2 = (cellHeight * i);
            canvas.drawLine(x1, y1, x2, y2, paint);
        }
        for (int i = 0; i < mColumns; ++i) {
            float x1 = (cellWidth * i);
            float y1 = 0;
            float x2 = (cellWidth * i);
            float y2 = (boardHeight);
            canvas.drawLine(x1, y1, x2, y2, paint);
        }

//        Paint paint = new Paint();
//        paint.setColor(Color.WHITE);
//        canvas.drawCircle(52, 52, 50, paint);
//        drawingRect.left = (cellWidth * j);
//        drawingRect.top = (cellHeight * i);
//        drawingRect.right = (cellWidth * (j + 1));
//        drawingRect.bottom = (cellHeight * (i + 1));
//        canvas.drawRect(drawingRect, paint);
    }

//    public void drawDots(Canvas canvas, int boardWidth, int boardHeight) {
//        float y = 0;
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        int cellWidth = boardWidth / mColumns;
//
//        for (int i = 1; i <= mColumns; ++i) {
//            float x = ((cellWidth * i) - (cellWidth/2));
//            canvas.drawCircle(x, y, 1, paint);
//        }
//    }

    public void drawFallingShapes(Canvas canvas, int boardWidth, int boardHeight) {
        Shape[][] shapesGrid = mFallingShapes.getShapesGrid();
        int cellWidth = boardWidth / mColumns;
        int cellHeight = boardHeight / mRows;
        for (int i = 0; i < shapesGrid.length; ++i) {
            for (int j = 0; j < shapesGrid[0].length; ++j) {
                int x = ((cellWidth * j) - (cellWidth/2));
                int y = (cellHeight * i) + (cellHeight/2);
                drawShape(canvas, x, y, shapesGrid[i][j]);
            }
        }
    }

    public void drawShape(Canvas canvas, int x, int y, Shape shape) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        if (shape.getShapeType() == ShapeType.SQUARE) {
            Rect drawingRect = new Rect();
            drawingRect.left = x - (shape.getImportantLength()/2);
            drawingRect.top = y - (shape.getImportantLength()/2);
            drawingRect.right = x + (shape.getImportantLength()/2);
            drawingRect.bottom = y + (shape.getImportantLength()/2);
            paint.setColor(Color.RED);
            canvas.drawRect(drawingRect, paint);
        } else if (shape.getShapeType() == ShapeType.CIRCLE) {
            paint.setColor(Color.BLUE);
            canvas.drawCircle(x, y, shape.getImportantLength()/2, paint);
        } else if (shape.getShapeType() == ShapeType.TRIANGLE) {
//            Path path = new Path();
//            path.moveTo(x - (shape.getImportantLength()/2), y + (shape.getImportantLength()/2));
//            path.lineTo(x + (shape.getImportantLength()/2), y + (shape.getImportantLength()/2));
//            path.moveTo(x + (shape.getImportantLength()/2), y + (shape.getImportantLength()/2));
//            path.lineTo(x + (shape.getImportantLength()/2), y - (shape.getImportantLength()/2));
//            path.moveTo(x + (shape.getImportantLength()/2), y - (shape.getImportantLength()/2));
//            path.lineTo(x - (shape.getImportantLength()/2), y + (shape.getImportantLength()/2));
//            path.close();
//
//            canvas.drawPath(path, paint);
            Rect drawingRect = new Rect();
            drawingRect.left = x - (shape.getImportantLength()/2);
            drawingRect.top = y - (shape.getImportantLength()/2);
            drawingRect.right = x + (shape.getImportantLength()/2);
            drawingRect.bottom = y + (shape.getImportantLength()/2);
            paint = new Paint();
            paint.setColor(Color.GREEN);
            canvas.drawRect(drawingRect, paint);
        }
    }

    public FallingShapes getFallingShapes() {
        return mFallingShapes;
    }

}
