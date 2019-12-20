package bsu.rfe.java.group8.lab4.KONONOK.varC3;

import java.awt.*;
import java.awt.geom.*;
import java.text.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Stack;

import javax.swing.JPanel;

@SuppressWarnings({ "serial" })
public class GraphicsDisplay extends JPanel {

    static class GraphPoint {
        double xd;
        double yd;
        int x;
        int y;
        int n;
    }

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
    private int[][] graphicsDataI;
    private boolean showAxis = true;
    private boolean showMarkers = true;
    private boolean transform = false;
    private boolean zoom=false;
    private boolean selMode = false;
    private boolean dragMode = false;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double scale;
    private double scaleX;
    private double scaleY;
    private int gran = 0;
    private DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
    private BasicStroke graphicsStroke;
    private BasicStroke graphics2Stroke;
    private BasicStroke axisStroke;
    private BasicStroke selStroke;
    private BasicStroke markerStroke;
    private Font axisFont;
    private Font hintFont;
    private Font captionFont;
    private int mausePX = 0;
    private int mausePY = 0;
    private GraphPoint SMP;
    private Rectangle2D.Double rect;
    private Stack<Zone> stack = new Stack<>();

    GraphicsDisplay() {
        setBackground(Color.WHITE);
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[] {6,2,6,2,6,2,2,2,2,2,2,2}, 0.0f);
        graphics2Stroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[] {6,6}, 0.0f);
        axisStroke = new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        selStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 8, 8 }, 0.0f);
        axisFont = new Font("Serif", Font.BOLD, 20);
        hintFont = new Font("Serif", Font.BOLD, 10);

        captionFont = new Font("Serif", Font.BOLD, 10);
        MouseMotionHandler mouseMotionHandler = new MouseMotionHandler();
        addMouseMotionListener(mouseMotionHandler);
        addMouseListener(mouseMotionHandler);
        rect = new Rectangle2D.Double();
        zone.use = false;
    }

    void showGraphics(Double[][] Data, boolean nomer) {
        if (nomer) {
            if (graphicsData2.length == 0) {
                graphicsData1 = Data;
                graphicsData = Data;
                graphicsDataI = new int[graphicsData.length][2];
                repaint();
            } else {
                graphicsData1 = Data;
                Double[][] newData = new Double[graphicsData2.length + Data.length][];
                System.arraycopy(Data, 0, newData, 0, Data.length);
                System.arraycopy(graphicsData2, 0, newData, Data.length, graphicsData2.length);
                graphicsData = newData;
                graphicsDataI = new int[graphicsData.length][2];
                repaint();
            }
        } else {
            if (graphicsData1.length == 0) {
                graphicsData2 = Data;
                graphicsData = Data;
                graphicsDataI = new int[graphicsData.length][2];
                repaint();
            } else {
                graphicsData2 = Data;
                Double[][] newData = new Double[graphicsData1.length + Data.length][];
                System.arraycopy(graphicsData1, 0, newData, 0, graphicsData1.length);
                System.arraycopy(Data, 0, newData, graphicsData1.length, Data.length);
                graphicsData = newData;
                graphicsDataI = new int[graphicsData.length][2];
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

        scaleX = 1.0 / (maxX - minX);
        scaleY = 1.0 / (maxY - minY);
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
        if(!zoom){
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
        }
        Graphics2D canvas = (Graphics2D) g;
        if (showAxis)
            paintAxis(canvas);
        paintGraphics(canvas);
        if (showMarkers)
            paintMarkers(canvas);
        if (SMP != null)
            paintHint(canvas);
        if (selMode) {
            canvas.setColor(Color.BLACK);
            canvas.setStroke(selStroke);
            canvas.draw(rect);
        }
    }

    private void paintHint(Graphics2D canvas) {
        canvas.setColor(Color.MAGENTA);
        canvas.setFont(hintFont);
        StringBuilder label = new StringBuilder();
        label.append("X=");
        label.append(formatter.format((SMP.xd)));
        label.append(", Y=");
        label.append(formatter.format((SMP.yd)));
        FontRenderContext context = canvas.getFontRenderContext();
        Rectangle2D bounds = captionFont.getStringBounds(label.toString(),context);
        if (!transform) {
            int dy = -10;
            int dx = +7;
            if (SMP.y < bounds.getHeight())
                dy = +13;
            if (getWidth() < bounds.getWidth() + SMP.x + 20)
                dx = -(int) bounds.getWidth() - 15;
            canvas.drawString (label.toString(), SMP.x + dx, SMP.y + dy);
        } else {
            int dy = 10;
            int dx = -7;
            if (SMP.x < 10)
                dx = +13;
            if (SMP.y < bounds.getWidth() + 20)
                dy = -(int) bounds.getWidth() - 15;
            //canvas.rotate(- Math.PI / 2);
            //canvas.translate(getHeight() - getWidth(), - getWidth()/2 - getHeight()/2);
            canvas.drawString (label.toString(), getHeight() - SMP.y + dy, SMP.x + dx);

        }
    }

    private void paintGraphics(Graphics2D canvas) {
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.RED);
        GeneralPath graphics = new GeneralPath();
        for (int i = 0; i < graphicsData1.length; i++) {
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
            graphicsDataI[i][0] = (int) point.getX();
            graphicsDataI[i][1] = (int) point.getY();
            if (transform) {
                graphicsDataI[i][0] = (int) point.getY();
                graphicsDataI[i][1] = getHeight() - (int) point.getX();
            }
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
            graphicsDataI[i][0] = (int) point.getX();
            graphicsDataI[i][1] = (int) point.getY();
            if (transform) {
                graphicsDataI[i][0] = (int) point.getY();
                graphicsDataI[i][1] = getHeight() - (int) point.getX();
            }
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
        if(!zoom)
            return new Point2D.Double(deltaX * scale, deltaY * scale);
        else
            return new Point2D.Double(deltaX * scaleX, deltaY * scaleY);
    }

    private Point2D.Double pointToXY(int x, int y) {
        Point2D.Double p = new Point2D.Double();
        if (!transform) {
            p.x = x / scale + minX;
            int q = (int) xyToPoint(0, 0).y;
            p.y = maxY - maxY * ((double) y / (double) q);
        } else {
            if(!zoom){
                p.y = -x / scale + (maxY);
                p.x = -y / scale + maxX;
            }else{
                p.y = -x / scaleY + (maxY);
                p.x = -y / scaleX + maxX;
            }
        }
        return p;
    }

    public class MouseMotionHandler implements MouseMotionListener, MouseListener {
        private double comparePoint(Point p1, Point p2) {
            return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
        }

        private GraphPoint find(int x, int y) {
            GraphPoint smp = new GraphPoint();
            double r;
            for (int i = 0; i < graphicsData.length; i++) {
                Point p = new Point();
                p.x = x;
                p.y = y;
                Point p2 = new Point();
                if (!transform) {
                    p2.x = graphicsDataI[i][0];
                    p2.y = graphicsDataI[i][1];
                } else {
                    p2.x = getWidth() - graphicsDataI[i][0];
                    p2.y = getHeight() - graphicsDataI[i][1];
                }
                r = comparePoint(p, p2);
                if (r < 7.0) {
                    smp.x = graphicsDataI[i][0];
                    smp.y = graphicsDataI[i][1];
                    smp.xd = graphicsData[i][0];
                    smp.yd = graphicsData[i][1];
                    smp.n = i;
                    return smp;
                }
            }
            return null;
        }

        public void mouseMoved(MouseEvent ev) {
            GraphPoint smp;
            smp = find(ev.getX(), ev.getY());
            if (smp != null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                SMP = smp;
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                SMP = null;
            }
            repaint();
        }

        public void mouseDragged(MouseEvent e) {
            if (selMode) {
                if (!transform)
                    rect.setFrame(mausePX, mausePY, e.getX() - rect.getX(),
                            e.getY() - rect.getY());
                else {
                    rect.setFrame(-mausePY + getHeight(), mausePX, -e.getY()
                            + mausePY, e.getX() - mausePX);
                }
                repaint();
            }
            if (dragMode) {
                if (!transform) {
                    if(pointToXY(e.getX(), e.getY()).y < maxY && pointToXY(e.getX(), e.getY()).y > minY) {
                        graphicsData[SMP.n][1] = pointToXY(e.getX(), e.getY()).y;
                        SMP.yd = pointToXY(e.getX(), e.getY()).y;
                        SMP.y = e.getY();
                    }
                } else {
                    if(pointToXY(e.getX(), e.getY()).y < maxY && pointToXY(e.getX(), e.getY()).y > minY){
                        graphicsData[SMP.n][1] = pointToXY(getWidth() - e.getX(), getHeight() - e.getY()).y;
                        SMP.yd = pointToXY(getWidth() - e.getX(), getHeight() - e.getY()).y;
                        SMP.x = getWidth() - e.getX();
                    }
                }
                repaint();
            }
        }

        public void mouseClicked(MouseEvent e) {
            if (e.getButton() != 3)
                return;
            try {
                zone = stack.pop();
            } catch (EmptyStackException ignored) {
            }
            if(stack.empty())
                zoom=false;
            repaint();
        }

        public void mouseEntered(MouseEvent arg0) {
        }

        public void mouseExited(MouseEvent arg0) {
        }

        public void mousePressed(MouseEvent e) {
            if (e.getButton() != 1)
                return;
            if (SMP != null) {
                selMode = false;
                dragMode = true;
            } else {
                dragMode = false;
                selMode = true;
                mausePX = e.getX();
                mausePY = e.getY();
                if (!transform)
                    rect.setFrame(e.getX(), e.getY(), 0, 0);
                else
                    rect.setFrame(e.getX(), e.getY(), 0, 0);
            }
        }

        public void mouseReleased(MouseEvent e) {
            rect.setFrame(0, 0, 0, 0);
            if (e.getButton() != 1) {
                repaint();
                return;
            }
            if (selMode) {
                if (!transform) {
                    if (e.getX() <= mausePX || e.getY() <= mausePY)
                        return;
                    int eY = e.getY();
                    int eX = e.getX();
                    if (eY > getHeight())
                        eY = getHeight();
                    if (eX > getWidth())
                        eX = getWidth();
                    double MAXX = pointToXY(eX, 0).x;
                    double MINX = pointToXY(mausePX, 0).x;
                    double MAXY = pointToXY(0, mausePY).y;
                    double MINY = pointToXY(0, eY).y;
                    stack.push(zone);
                    zone = new Zone();
                    zone.use = true;
                    zone.MAXX = MAXX;
                    zone.MINX = MINX;
                    zone.MINY = MINY;
                    zone.MAXY = MAXY;
                    selMode = false;
                    zoom=true;
                } else {
                    if (pointToXY(mausePX, 0).y <= pointToXY(e.getX(), 0).y
                            || pointToXY(0, e.getY()).x <= pointToXY(0, mausePY).x)
                        return;
                    int eY = e.getY();
                    int eX = e.getX();
                    if (eY < 0)
                        eY = 0;
                    if (eX > getWidth())
                        eX = getWidth();
                    stack.push(zone);
                    zone = new Zone();
                    zone.use = true;
                    zone.MAXY = pointToXY(mausePX, 0).y;
                    zone.MAXX = pointToXY(0, eY).x;
                    zone.MINX = pointToXY(0, mausePY).x;
                    zone.MINY = pointToXY(eX, 0).y;
                    selMode = false;
                    zoom=true;
                }

            }
            repaint();
        }
    }
}
