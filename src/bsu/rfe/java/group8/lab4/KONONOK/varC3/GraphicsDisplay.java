package bsu.rfe.java.group8.lab4.KONONOK.varC3;

import java.awt.*;
import java.awt.geom.*;
import java.awt.font.FontRenderContext;

import javax.swing.JPanel;

@SuppressWarnings({ "serial" })
public class GraphicsDisplay extends JPanel {

       static class Zone {
        double MAXY;
        double MINY;
        double MAXX;
        double MINX;
        boolean use;
    }

    private Zone zone = new Zone();
    private Double[][] graphicsData;
    private Double[][] graphicsData1 = new Double[0][];
    private Double[][] graphicsData2 = new Double[0][];
    private boolean showAxis = true;
    private boolean showMarkers = true;
    private boolean transform = false;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double scale;
    private BasicStroke graphicsStroke;
    private BasicStroke graphics2Stroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    private Font axisFont;

    GraphicsDisplay() {
        setBackground(Color.WHITE);
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[] {6,2,6,2,6,2,2,2,2,2,2,2}, 0.0f);
        graphics2Stroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[] {6,6}, 0.0f);
        axisStroke = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        axisFont = new Font("Serif", Font.BOLD, 20);

    }

    void showGraphics(Double[][] Data, boolean nomer) {
        if (nomer) {
            if (graphicsData2.length == 0) {
                graphicsData1 = Data;
                graphicsData = Data;
                repaint();
            } else {
                graphicsData1 = Data;
                Double[][] newData = new Double[graphicsData2.length + Data.length][];
                System.arraycopy(Data, 0, newData, 0, Data.length);
                System.arraycopy(graphicsData2, 0, newData, Data.length, graphicsData2.length);
                graphicsData = newData;
                repaint();
            }
        } else {
            if (graphicsData1.length == 0) {
                graphicsData2 = Data;
                graphicsData = Data;
                repaint();
            } else {
                graphicsData2 = Data;
                Double[][] newData = new Double[graphicsData1.length + Data.length][];
                System.arraycopy(graphicsData1, 0, newData, 0, graphicsData1.length);
                System.arraycopy(Data, 0, newData, graphicsData1.length, Data.length);
                graphicsData = newData;
                repaint();
            }
        }
    }

    void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    void setTransform(boolean transform) {
        this.transform = transform;
        repaint();
    }

    int getDataLenght() {
        return graphicsData.length;
    }

    double getValue(int i, int j, boolean nomer) {
        if (nomer)
            return graphicsData1[i][j];
        else
            return  graphicsData2[i][j];
    }

    void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graphicsData == null || graphicsData.length == 0)
            return;
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length - 1][0];

        if (zone.use) {
            minX = zone.MINX;
        }
        if (zone.use) {
            maxX = zone.MAXX;
        }
        minY = graphicsData[0][1];
        maxY = minY;

        for (int i = 1; i < graphicsData.length; i++) {
            if (graphicsData[i][1] < minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1] > maxY) {
                maxY = graphicsData[i][1];
            }
        }
        if (zone.use) {
            minY = zone.MINY;
        }
        if (zone.use) {
            maxY = zone.MAXY;
        }

        double scaleX = 1.0 / (maxX - minX);
        double scaleY = 1.0 / (maxY - minY);
        if (!transform)
            scaleX *= getSize().getWidth();
        else
            scaleX *= getSize().getHeight();
        if (!transform)
            scaleY *= getSize().getHeight();
        else
            scaleY *= getSize().getWidth();
        if (transform) {
            ((Graphics2D) g).rotate(Math.PI / 2);
            g.translate(0, -getWidth());
        }
        scale = Math.min(scaleX, scaleY);

            if (scale == scaleX) {
                double yIncrement;
                if (!transform)
                    yIncrement = (getSize().getHeight() / scale - (maxY - minY)) / 2;
                else
                    yIncrement = (getSize().getWidth() / scale - (maxY - minY)) / 2;
                maxY += yIncrement;
                minY -= yIncrement;
            }
            if (scale == scaleY) {
                double xIncrement;
                if (!transform) {
                    xIncrement = (getSize().getWidth() / scale - (maxX - minX)) / 2;
                    maxX += xIncrement;
                    minX -= xIncrement;
                } else {
                    xIncrement = (getSize().getHeight() / scale - (maxX - minX)) / 2;
                    maxX += xIncrement;
                    minX -= xIncrement;
                }
            }
        Graphics2D canvas = (Graphics2D) g;
        if (showAxis)
            paintAxis(canvas);
        paintGraphics(canvas);
        if (showMarkers)
            paintMarkers(canvas);
    }

    private void paintGraphics(Graphics2D canvas) {
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.RED);
        GeneralPath graphics = new GeneralPath();
        for (int i = 0; i < graphicsData1.length; i++) {
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
                   if (i > 0) {
                graphics.lineTo(point.getX(), point.getY());
            } else {
                graphics.moveTo(point.getX(), point.getY());
            }
        }
        canvas.draw(graphics);
        canvas.setStroke(graphics2Stroke);
        canvas.setColor(Color.BLUE);
        GeneralPath graphics2 = new GeneralPath();
        for (int i = graphicsData1.length; i < graphicsData.length; i++) {
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
              if (i != graphicsData1.length) {
                graphics2.lineTo(point.getX(), point.getY());
            } else {
                graphics2.moveTo(point.getX(), point.getY());
            }
        }
        canvas.draw(graphics2);
    }

    private void paintMarkers(Graphics2D canvas) {
        canvas.setStroke(markerStroke);
        for (Double[] point : graphicsData) {
            canvas.setStroke(axisStroke);
            int res = Math.abs(point[1].intValue()), sum = 0;
            while (res > 0) {
                sum += res % 10;
                res = res / 10;
            }
            if (sum < 10) {
                canvas.setColor(Color.BLACK);
                canvas.setPaint(Color.BLACK);
            } else {
                canvas.setColor(Color.GREEN);
                canvas.setPaint(Color.GREEN);
            }
            Ellipse2D.Double marker = new Ellipse2D.Double();
            Point2D.Double center = xyToPoint(point[0], point[1]);
            Point2D.Double corner = shiftPoint(center, 5, 5);
            marker.setFrameFromCenter(center, corner);
            canvas.draw(marker);
            canvas.draw(new Line2D.Double(shiftPoint(center, 0, 5),
                    shiftPoint(center, 0, -5)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 5, 0),
                    shiftPoint(center, -5, 0)));
        }
    }

    private Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY) {
        Point2D.Double dest = new Point2D.Double();
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }

    private void paintAxis(Graphics2D canvas) {
        canvas.setStroke(axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);
        FontRenderContext context = canvas.getFontRenderContext();
        if (minX <= 0.0 && maxX >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() + 5, arrow.getCurrentPoint().getY() + 20);
            arrow.lineTo(arrow.getCurrentPoint().getX() - 10, arrow.getCurrentPoint().getY());
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            canvas.drawString("y", (float) labelPos.getX() + 10, (float) (labelPos.getY() - bounds.getY()) + 10);
        }
        if (minY <= 0.0 && maxY >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() - 20, arrow.getCurrentPoint().getY() - 5);
            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY() + 10);
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            canvas.drawString("x", (float) (labelPos.getX() - bounds.getWidth() - 20), (float) (labelPos.getY() + bounds.getY()));
        }
    }

    private Point2D.Double xyToPoint(double x, double y) {
        double deltaX = x - minX;
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX * scale, deltaY * scale);
    }
}
