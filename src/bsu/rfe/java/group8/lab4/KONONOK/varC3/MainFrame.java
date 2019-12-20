package bsu.rfe.java.group8.lab4.KONONOK.varC3;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private boolean fileLoaded = false;
    private JFileChooser fileChooser = null;
    private JCheckBoxMenuItem showAxisMenuItem;
    private JCheckBoxMenuItem showMarkersMenuItem;
    private JCheckBoxMenuItem reformCoordinateItem;
    private GraphicsDisplay display = new GraphicsDisplay();

    private MainFrame() {
        super("Построение графиков функций на основе заранее подготовленных файлов");
        setSize(WIDTH, HEIGHT);
        Toolkit kit = Toolkit.getDefaultToolkit();
        setLocation((kit.getScreenSize().width - WIDTH)/2,
                (kit.getScreenSize().height - HEIGHT)/2);
        setExtendedState(MAXIMIZED_BOTH);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);

        Action openGraphicsAction = new AbstractAction("Открыть файл с графиком 1") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser==null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    openGraphics(fileChooser.getSelectedFile(), true);
                    fileLoaded = true;
                }
            }
        };
        fileMenu.add(openGraphicsAction);

        Action openGraphics2Action = new AbstractAction("Открыть файл с графиком 2") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser==null) {
                    fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File("."));
                }
                if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
                    openGraphics(fileChooser.getSelectedFile(), false);
                    fileLoaded = true;
                }
            }
        };
        fileMenu.add(openGraphics2Action);

        Action saveGraphicsAction = new AbstractAction("Сохранить файл с графиком 1 ") {
            public void actionPerformed(ActionEvent event) {
                fileChooser.setCurrentDirectory(new File("~"));
                if (fileChooser.showSaveDialog(MainFrame.this)==JFileChooser.APPROVE_OPTION)
                    saveGraphics(true);
            }
        };
        fileMenu.add(saveGraphicsAction);

        Action saveGraphics2Action = new AbstractAction("Сохранить файл с графиком 2 ") {
            public void actionPerformed(ActionEvent event) {
                fileChooser.setCurrentDirectory(new File("~"));
                if (fileChooser.showSaveDialog(MainFrame.this)==JFileChooser.APPROVE_OPTION)
                    saveGraphics(false);
            }
        };
        fileMenu.add(saveGraphics2Action);

        JMenu graphicsMenu = new JMenu("График");
        menuBar.add(graphicsMenu);

        Action showAxisAction = new AbstractAction("Показывать оси координат") {
            public void actionPerformed(ActionEvent event) {
                display.setShowAxis(showAxisMenuItem.isSelected());
            }
        };
        showAxisMenuItem = new JCheckBoxMenuItem(showAxisAction);
        graphicsMenu.add(showAxisMenuItem);
        showAxisMenuItem.setSelected(true);

        Action showMarkersAction = new AbstractAction("Показывать маркеры точек") {
            public void actionPerformed(ActionEvent event) {
                display.setShowMarkers(showMarkersMenuItem.isSelected());
            }
        };
        showMarkersMenuItem = new JCheckBoxMenuItem(showMarkersAction);
        graphicsMenu.add(showMarkersMenuItem);
        showMarkersMenuItem.setSelected(true);

        Action reformCoordinateAction = new AbstractAction("Преобразовать координаты") {
            public void actionPerformed(ActionEvent event) {
                display.setTransform(reformCoordinateItem.isSelected());
            }
        };
        reformCoordinateItem = new JCheckBoxMenuItem(reformCoordinateAction);
        graphicsMenu.add(reformCoordinateItem);
        reformCoordinateItem.setSelected(false);

        graphicsMenu.addMenuListener(new GraphicsMenuListener());
        getContentPane().add(display, BorderLayout.CENTER);
    }

    private void saveGraphics(boolean nomer) {
        File file=fileChooser.getSelectedFile();
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
            for (int i = 0; i<display.getDataLenght(); i++) {
                out.writeDouble(display.getValue(i,0, nomer));
                out.writeDouble(display.getValue(i,1, nomer));
            }
            out.close();
        } catch (Exception e) {
            System.out.println("Не удалость создать файл");
        }
    }

    private void openGraphics(File selectedFile, boolean nomer) {
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(selectedFile));
            Double[][] graphicsData = new Double[in.available()/(Double.SIZE/8)/2][];
            int i = 0;
            while (in.available()>0) {
                double x = in.readDouble();
                double y = in.readDouble();
                graphicsData[i++] = new Double[] {x, y};
            }
            if (graphicsData.length > 0) {
                display.showGraphics(graphicsData, nomer);
                display.repaint();
            }
            in.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(MainFrame.this, "Указанный файл не найден", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(MainFrame.this, "Ошибка чтения координат точек из файла", "Ошибка загрузки данных", JOptionPane.WARNING_MESSAGE);
        }
    }

        public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private class GraphicsMenuListener implements MenuListener {
        public void menuSelected(MenuEvent e) {
            showAxisMenuItem.setEnabled(fileLoaded);
            showMarkersMenuItem.setEnabled(fileLoaded);
            reformCoordinateItem.setEnabled(fileLoaded);
        }

        public void menuDeselected(MenuEvent e) {
        }

        public void menuCanceled(MenuEvent e) {
        }
    }
}

