import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;

import javax.swing.*;

public class TimeTable extends JFrame implements ActionListener {

	private JPanel screen = new JPanel(), tools = new JPanel();
	private JButton tool[];
	private JTextField field[];
	private CourseArray courses;
	private Color CRScolor[] = {Color.RED, Color.GREEN, Color.BLACK};
	private Autoassociator autoassociator;
	private BufferedWriter logWriter;

	

	public TimeTable() {
		super("Dynamic Time Table");
		setSize(500, 800);
		setLayout(new FlowLayout());
		
		screen.setPreferredSize(new Dimension(400, 800));
		add(screen);
		
		setTools();
		add(tools);
		
		setVisible(true);

		try {
            logWriter = new BufferedWriter(new FileWriter("timeTableLog.txt", true));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setVisible(true);
	}
	
	
	public void setTools() {
		String capField[] = {"Slots:", "Courses:", "Clash File:", "Iters:", "Shift:"};
		field = new JTextField[capField.length];
		
		String capButton[] = {"Load", "Start", "Step", "Print", "Exit", "Continue", "Train", "Update"};
		tool = new JButton[capButton.length];
		
		tools.setLayout(new GridLayout(2 * capField.length + capButton.length, 1));
		
		for (int i = 0; i < field.length; i++) {
			tools.add(new JLabel(capField[i]));
			field[i] = new JTextField(5);
			tools.add(field[i]);
		}
		
		for (int i = 0; i < tool.length; i++) {
			tool[i] = new JButton(capButton[i]);
			tool[i].addActionListener(this);
			tools.add(tool[i]);
		}
		
		field[0].setText("20");//17
		field[1].setText("261");//381
		field[2].setText("tre-s-92.stu");//"lse-f-91.stu"
		field[3].setText("1");
	}
	
	public void draw() {
		Graphics g = screen.getGraphics();
		int width = Integer.parseInt(field[0].getText()) * 10;
		for (int courseIndex = 1; courseIndex < courses.length(); courseIndex++) {
			g.setColor(CRScolor[courses.status(courseIndex) > 0 ? 0 : 1]);
			g.drawLine(0, courseIndex, width, courseIndex);
			g.setColor(CRScolor[CRScolor.length - 1]);
			g.drawLine(10 * courses.slot(courseIndex), courseIndex, 10 * courses.slot(courseIndex) + 10, courseIndex);
		}
	}
	
	private int getButtonIndex(JButton source) {
		int result = 0;
		while (source != tool[result]) result++;
		return result;
	}

	private void log(String message) {
        try {
            logWriter.write(message + "\n");
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configureLogger(int slots, int iterations, int shifts) {
        try {
            logWriter.write("Starting timetable algorithm with Slots: " + slots + ", Iterations: " + iterations + ", Shifts: " + shifts + "\n");
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public void actionPerformed(ActionEvent click) {
		int min, step, clashes;
		
		switch (getButtonIndex((JButton) click.getSource())) {
		case 0:
			int slots = Integer.parseInt(field[0].getText());
			courses = new CourseArray(Integer.parseInt(field[1].getText()) + 1, slots);
			courses.readClashes(field[2].getText());
			autoassociator = new Autoassociator(courses);
			configureLogger(slots, Integer.parseInt(field[3].getText()), Integer.parseInt(field[4].getText()));

			draw();
			break;

		case 1:
			min = Integer.MAX_VALUE;
			step = 0;
			autoassociator = new Autoassociator(courses);
			for (int i = 1; i < courses.length(); i++) courses.setSlot(i, 0);
	
			for (int iteration = 1; iteration <= Integer.parseInt(field[3].getText()); iteration++) {
				courses.iterate(Integer.parseInt(field[4].getText()));
				draw();
				clashes = courses.clashesLeft();
				if (clashes < min) {
					min = clashes;
					step = iteration;
				}		               

				draw();
				

   
			}
			log("Clashes left = " + courses.clashesLeft());
			log("Final state: Min clashes = " + min + " at step " + step);
			System.out.println("Shift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);
			courses.printSlotStatus();
			setVisible(true);
			
			break;
		case 2:
			courses.iterate(Integer.parseInt(field[4].getText()));
			draw();
			break;
		case 3:
			System.out.println("Exam\tSlot\tClashes");
			for (int i = 1; i < courses.length(); i++)
				System.out.println(i + "\t" + courses.slot(i) + "\t" + courses.status(i));
			break;
		case 4:
			System.exit(0);
			break;
		case 5:
			min = Integer.MAX_VALUE;
			step = 0;

			log("Continuing timetable algorithm with Slots: " + field[0].getText() + ", Iterations: " + Integer.parseInt(field[3].getText()) + ", Shifts: " + Integer.parseInt(field[4].getText()));
            
		
			for (int iteration = 1; iteration <= Integer.parseInt(field[3].getText()); iteration++) {
				courses.iterate(Integer.parseInt(field[4].getText()));
				draw();
				clashes = courses.clashesLeft();
				if (clashes < min) {
					min = clashes;
					step = iteration;
				}
						
                
			}
			log("Clashes left = " + courses.clashesLeft());
			log("Final state: Min clashes = " + min + " at step " + step);
			System.out.println("Shift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);
			courses.printSlotStatus();
			setVisible(true);
			break;

			case 6: 
			
					for (int j = 1; j < courses.length(); j++) {
						if (courses.status(j) == 0) {
							int[] timeSlot = courses.getTimeSlot(j);
							autoassociator.training(timeSlot);
							log("Trained with time slot: " + j);
						}
					}
					break;

			case 7:
					min = Integer.MAX_VALUE;
					step = 0;
					slots = Integer.parseInt(field[0].getText());
					for (int iter = 1; iter <= Integer.parseInt(field[3].getText()); iter++) {
						if (iter%10==0){
							Random random = new Random();
        					int index = random.nextInt(slots);
							log("Updating time slot: " + index);
							autoassociator.unitUpdate(courses.getTimeSlot(index), iter);

						}
						courses.iterate(Integer.parseInt(field[4].getText()));
						draw();
						clashes = courses.clashesLeft();
						if(clashes < min ){
							min = clashes;
							step = iter;

						}
						log("After Autoassociator update: Min clashes = " + min + " at step " + step);
						System.out.println("Shift = " + field[4].getText() + "\tMin clashes = " + min + "\tat step " + step);
						courses.printSlotStatus();
						setVisible(true);
						


					}

			

		}
		
	}

	public static void main(String[] args) {
		new TimeTable();
	}
}
