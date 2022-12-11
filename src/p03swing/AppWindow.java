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
	Checkbox checkBox1 = new Checkbox("Median", checkBoxGroup, false);
	checkBox1.setBounds(40,40, 100,50);
	Checkbox checkBox2 = new Checkbox("Mean", checkBoxGroup, true);
	checkBox2.setBounds(40,75, 100,50);
	add(checkBox1);
	add(checkBox2);

	checkBox1.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			System.out.println("CHOICE1");
		}
	});
	checkBox2.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
			System.out.println("CHOICE2");
		}
	});

	Label labelMode = new Label ("Mód filtru:");
	Label filterSize = new Label ("Velikost filtru: 3");

	// set the location of label
	labelMode.setBounds(30, 20, 100, 30);
	filterSize.setBounds(30, 130, 100, 30);

	// adding labels to the frame
	add(labelMode);


	Scrollbar scrollbarFilterSize = new Scrollbar(HORIZONTAL, 0, 0, 0,  5);

	// setting the position of scroll bar
	scrollbarFilterSize.setBounds (40, 160, 100, 20);
	scrollbarFilterSize.setBackground(Color.white);

	scrollbarFilterSize.addAdjustmentListener(new AdjustmentListener() {
		public void adjustmentValueChanged(AdjustmentEvent e) {
			int filterValue = 3+2*scrollbarFilterSize.getValue();
			filterSize.setText("Velikost filtru: " + filterValue);
			Renderer.sayMeow(filterValue);
		}
	});

	add(scrollbarFilterSize);
	add(filterSize);

	Button buttonSelectImage = new Button("Vybrat obrázek");

	// set the position for the button in frame
	buttonSelectImage.setBounds(40,200,100,30);
	add(buttonSelectImage);

	buttonSelectImage.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent e) {
			Renderer.selectImage();
		}
	});

	// set size, layout and visibility of frame
	setSize(400,400);
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

