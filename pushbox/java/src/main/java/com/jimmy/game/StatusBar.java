package com.jimmy.game;

//refer to http://www.yaldex.com/java_tutorial/0338116072.htm.

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

class StatusPane extends JLabel {
	private Font paneFont = new Font("Arial", Font.PLAIN, 10);
	
	public StatusPane(String text) {
		setBackground(Color.LIGHT_GRAY); 
		setForeground(Color.black);
		//setFont(paneFont); 
		setHorizontalAlignment(LEFT); 
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setPreferredSize(new Dimension(170, 20));
		setText(text); 
	}
}

class StatusBar extends JPanel  {
	private StatusPane taskPane;
	private StatusPane stepPane;
	private JButton btnOption;

	public StatusBar() {
		//setLayout(new FlowLayout(FlowLayout.LEFT, 5, 3));
		setLayout(new BorderLayout());
		setBackground(Color.LIGHT_GRAY);
		setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

		taskPane = new StatusPane("You are at Task: ");
		stepPane = new StatusPane("You have moved 0 steps");
		btnOption = new JButton("Options");
		
		//ToDO: btnOption will make view lost key event.
		
		add(taskPane, BorderLayout.WEST); 
		add(stepPane, BorderLayout.CENTER);
		//add(btnOption, BorderLayout.EAST);
	}

	public void setTaskPane(String text) {
		  taskPane.setText(text);
	}

	public void setStepPane(String text) {
		  stepPane.setText(text);  
	}


}