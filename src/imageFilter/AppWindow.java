package imageFilter;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.Callable;

import javax.swing.JFrame;

import static javax.swing.SwingConstants.HORIZONTAL;


public class AppWindow extends JFrame {
    Callable<Integer> setApp;

    public AppWindow(Callable<Integer> setApp) {
        this.setApp = setApp;
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    setApp.call();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };

        //Vytvoření checkboxů pro Mean a Median filtr
        CheckboxGroup checkBoxGroup = new CheckboxGroup();
        Checkbox meanBox = new Checkbox("Mean", checkBoxGroup, true);
        Checkbox medianBox = new Checkbox("Median", checkBoxGroup, false);

        //Nastavení pozice
        meanBox.setBounds(30, 40, 100, 50);
        medianBox.setBounds(30, 140, 100, 50);

        add(meanBox);
        add(medianBox);

        //Vytvoření labelů
        Label labelMode = new Label("Mód filtru:");
        Label meanSize = new Label("Velikost filtru: 0 px");
        Label medianSize = new Label("Velikost filtru: 0 px");

        //Nastavení pozice
        labelMode.setBounds(30, 20, 100, 30);
        meanSize.setBounds(45, 80, 110, 30);
        medianSize.setBounds(45, 180, 110, 30);

        //Vytvoření tlačítka pro výběr obrázku
        Button buttonSelectImage = new Button("Vybrat obrázek");

        //Nastavení pozice
        buttonSelectImage.setBounds(50, 280, 100, 30);


        //Vytvoření scrollbarů pro Mean a Median filtr
        Scrollbar scrollbarMeanFilterSize = new Scrollbar(HORIZONTAL, 0, 0, 0, 8);
        Scrollbar scrollbarMedianFilterSize = new Scrollbar(HORIZONTAL, 0, 0, 0, 4); //z důvodu náročnosti Median filtru je maximální úroveň nastavena na 4

        //Nastavení pozice
        scrollbarMeanFilterSize.setBounds(40, 105, 100, 20);
        scrollbarMedianFilterSize.setBounds(40, 205, 100, 20);

        //Listenery pro scrollbarů velikosti filtrů
        scrollbarMeanFilterSize.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                int filterValue = 1 + 2 * scrollbarMeanFilterSize.getValue();
                if (scrollbarMeanFilterSize.getValue() == 0) {
                    filterValue = 0;
                }
                meanSize.setText("Velikost filtru: " + filterValue + " px");
                Renderer.changeFilterValue(filterValue);
            }
        });
        scrollbarMedianFilterSize.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                int filterValue = 1 + 2 * scrollbarMedianFilterSize.getValue();
                if (scrollbarMedianFilterSize.getValue() == 0) {
                    filterValue = 0;
                }
                medianSize.setText("Velikost filtru: " + filterValue + " px");
                Renderer.changeFilterValue(filterValue);
            }
        });

        //Nastavení scrollbarů
        scrollbarMeanFilterSize.setBackground(Color.white);
        scrollbarMedianFilterSize.setBackground(Color.white);
        scrollbarMedianFilterSize.setEnabled(false);

        //Přidání prvků do okna
        add(meanBox);
        add(medianBox);
        add(labelMode);
        add(scrollbarMedianFilterSize);
        add(scrollbarMeanFilterSize);
        add(medianSize);
        add(meanSize);
        add(buttonSelectImage);

        //Listenery pro choiceboxy. Při výběru jednoho filtru se zakáže posuvník toho druhého
        meanBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                scrollbarMedianFilterSize.setEnabled(false);
                scrollbarMeanFilterSize.setEnabled(true);
                int filterValue = 1 + 2 * scrollbarMeanFilterSize.getValue();
                if (scrollbarMeanFilterSize.getValue() == 0) {
                    filterValue = 0;
                }
                Renderer.changeMode(0, filterValue);
            }
        });
        medianBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                scrollbarMedianFilterSize.setEnabled(true);
                scrollbarMeanFilterSize.setEnabled(false);
                int filterValue = 1 + 2 * scrollbarMedianFilterSize.getValue();
                if (scrollbarMedianFilterSize.getValue() == 0) {
                    filterValue = 0;
                }
                Renderer.changeMode(1, filterValue);
            }
        });


        //Listener pro výběr obrázku
        buttonSelectImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Renderer.selectImage();
            }
        });

        setLayout(null);
        setVisible(true);

        //Listener pro ukončení programu
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }.start();
            }
        });

        pack();
        setVisible(true);
    }
}

