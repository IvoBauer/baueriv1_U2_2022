package p03swing;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.Callable;

import javax.swing.JFrame;
import javax.swing.JPanel;

import static javax.swing.SwingConstants.HORIZONTAL;


public class AppWindow extends JFrame{
	private JPanel contentPane;
	private int demoId = 1;
	static String[] names = { "lvl2advanced.p01gui.p01simple", "lvl5others.p02fractal.Renderer" };
	static int[] countMenuItems = { 1, 1 };
//	static String[] nameMenuItem = { "basic", "advanced" };
	Callable<Integer> setApp;
	/*private void setApp(Frame testFrame, String name) {
		if (thread != null) {
			glfwWaitEvents();
			quit.countDown();

			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			thread.dispose();
			// make new window with renderer in new thread
		}
		quit = new CountDownLatch(1);
		thread = new LwjglWindowThread(0, quit, new lvl2advanced.p01gui.p01simple.Renderer());
		window = thread.getWindow();
		thread.start();
	}*/

public AppWindow(Callable<Integer> setApp) {
	this.setApp = setApp;
	ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ae) {
			demoId = Integer
					.valueOf(ae.getActionCommand().substring(0, ae.getActionCommand().lastIndexOf('-') - 1).trim());
			try {
				setApp.call();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	CheckboxGroup checkBoxGroup = new CheckboxGroup();
	Checkbox checkBox1 = new Checkbox("Mean", checkBoxGroup, true);
	checkBox1.setBounds(30,40, 100,50);
	Checkbox checkBox2 = new Checkbox("Median", checkBoxGroup, false);
	checkBox2.setBounds(30,140, 100,50);
	add(checkBox1);
	add(checkBox2);


	Label labelMode = new Label ("Mód filtru:");
	Label meanSize = new Label ("Velikost filtru: 0 px");
	Label medianSize = new Label ("Velikost filtru: 0 px");

	// set the location of label
	labelMode.setBounds(30, 20, 100, 30);
	meanSize.setBounds(45, 80, 110, 30);
	medianSize.setBounds(45, 180, 110, 30);

	// adding labels to the frame
	add(labelMode);


	Scrollbar scrollbarMeanFilterSize = new Scrollbar(HORIZONTAL, 0, 0, 0,  8);
	scrollbarMeanFilterSize.setBounds (40, 105, 100, 20);
	scrollbarMeanFilterSize.setBackground(Color.white);

	scrollbarMeanFilterSize.addAdjustmentListener(new AdjustmentListener() {
		public void adjustmentValueChanged(AdjustmentEvent e) {
			int filterValue = 1+2*scrollbarMeanFilterSize.getValue();
			if (scrollbarMeanFilterSize.getValue() == 0){
				filterValue = 0;
			}
			meanSize.setText("Velikost filtru: " + filterValue + " px");
			Renderer.sayMeow(filterValue);
		}
	});

	Scrollbar scrollbarMedianFilterSize = new Scrollbar(HORIZONTAL, 0, 0, 0,  4);
	scrollbarMedianFilterSize.setBounds (40, 205, 100, 20);
	scrollbarMedianFilterSize.setBackground(Color.white);
	scrollbarMedianFilterSize.setEnabled(false);
	add(scrollbarMedianFilterSize);
	add(scrollbarMeanFilterSize);
	add(medianSize);
	add(meanSize);

	scrollbarMedianFilterSize.addAdjustmentListener(new AdjustmentListener() {
		public void adjustmentValueChanged(AdjustmentEvent e) {
			int filterValue = 1+2*scrollbarMedianFilterSize.getValue();
			if (scrollbarMedianFilterSize.getValue() == 0){
				filterValue = 0;
			}
			medianSize.setText("Velikost filtru: " + filterValue + " px");
			Renderer.sayMeow(filterValue);
		}
	});

	checkBox1.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			scrollbarMedianFilterSize.setEnabled(false);
			scrollbarMeanFilterSize.setEnabled(true);
			int filterValue = 1+2*scrollbarMeanFilterSize.getValue();
			if (scrollbarMeanFilterSize.getValue() == 0){
				filterValue = 0;
			}
			Renderer.changeMode(0,filterValue);
		}
	});

	checkBox2.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			scrollbarMedianFilterSize.setEnabled(true);
			scrollbarMeanFilterSize.setEnabled(false);
			int filterValue = 1+2*scrollbarMedianFilterSize.getValue();
			if (scrollbarMedianFilterSize.getValue() == 0){
				filterValue = 0;
			}
			Renderer.changeMode(1,filterValue);
		}
	});


	Button buttonSelectImage = new Button("Vybrat obrázek");

	// set the position for the button in frame
	buttonSelectImage.setBounds(40,280,100,30);
	add(buttonSelectImage);

	buttonSelectImage.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent e) {
			Renderer.selectImage();
		}
	});

	// set size, layout and visibility of frame
	setLayout(null);
	setVisible(true);

//	setMenuBar(menuBar);
	addWindowListener(new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			new Thread() {
				@Override
				public void run() {
					// if (animator.isStarted())
					// animator.stop();
					System.exit(0);
				}
			}.start();
		}
	});

	// testFrame.setTitle(ren.getClass().getName());

	pack();
	setVisible(true);
	
	}
}

