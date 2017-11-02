package maze_generator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUI extends JFrame{
	private JPanel panel;
	private JPanel canvas;
	private JLabel labelWidth;
	private JLabel labelHeight;
	private JTextField textFieldWidth;
	private JTextField textFieldHeight;
	private JButton buttonGenerateMaze;
	
	private Passage[][] board = new Passage[0][0];
	
	int passageWidth, passageHeight;
	
	public GUI(String title, int width, int height){
		super(title);
		setLayout(null);
		setSize(width, height);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new JPanel();
		panel.setBounds(0, 0, getWidth() - 16, getHeight() - 38);
		panel.setBackground(Color.LIGHT_GRAY);
		panel.setLayout(null);
		add(panel);
		
		canvas = new Canvas();
		canvas.setBounds(5, 5, panel.getWidth() - 200, panel.getHeight() - 10);
		panel.add(canvas);
		
		labelWidth = new JLabel("Width: ");
		labelWidth.setFont(new Font(Font.SERIF, Font.PLAIN, 24));
		labelWidth.setBounds(canvas.getWidth() + 10, 10, 80, 35);
		panel.add(labelWidth);
		
		labelHeight = new JLabel("Height: ");
		labelHeight.setFont(new Font(Font.SERIF, Font.PLAIN, 24));
		labelHeight.setBounds(canvas.getWidth() + 10, (int) labelWidth.getLocation().getY() + labelWidth.getHeight() + 5, 80, 35);
		panel.add(labelHeight);
		
		textFieldWidth = new JTextField("33");
		textFieldWidth.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
		textFieldWidth.setBounds((int) labelWidth.getLocation().getX() + labelWidth.getWidth(), (int) labelWidth.getLocation().getY() + labelWidth.getHeight()/2 - 12, 80, 25);
		panel.add(textFieldWidth);
		
		textFieldHeight = new JTextField("39");
		textFieldHeight.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
		textFieldHeight.setBounds((int) labelHeight.getLocation().getX() + labelHeight.getWidth(), (int) labelHeight.getLocation().getY() + labelHeight.getHeight()/2 - 12, 80, 25);
		panel.add(textFieldHeight);
		
		buttonGenerateMaze = new JButton("Generate Maze");
		buttonGenerateMaze.setFont(new Font(Font.SERIF, Font.PLAIN, 24));
		buttonGenerateMaze.setBounds(panel.getWidth() - 190, panel.getHeight() - 50, 180, 40);
		buttonGenerateMaze.addActionListener(new ActionListener() {
			private List<int[]> lastPassages;
			private int[] previous = new int[]{0, 0};
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(textFieldWidth.getText().isEmpty() || textFieldHeight.getText().isEmpty()){
					JOptionPane.showMessageDialog(null, "Must enter the width and the height of the maze.", "Fields Empty", JOptionPane.ERROR_MESSAGE);
				}else{
					int mazeWidth = 5, mazeHeight = 7;
					try{
						mazeWidth = Integer.parseInt(textFieldWidth.getText());
						mazeHeight = Integer.parseInt(textFieldHeight.getText());
						
						if(mazeWidth <= 0 || mazeHeight <= 0){
							JOptionPane.showMessageDialog(null, "Both the width and the height fields must be greater than zero.", "One or Both Fields Are Illegal", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}catch(NumberFormatException numberFormatException){
						JOptionPane.showMessageDialog(null, "Both the width and the height fields must contain numbers.", "One or Both Fields Are Illegal", JOptionPane.ERROR_MESSAGE);
						numberFormatException.printStackTrace();
						return;
					}catch(Exception exception){
						exception.printStackTrace();
						return;
					}
					
					passageWidth = canvas.getWidth()/mazeWidth;
					passageHeight = canvas.getHeight()/mazeHeight;
					
					board = new Passage[mazeHeight][mazeWidth];
					board[0][0] = new Passage();
					
					lastPassages = new ArrayList<int[]>();
					
					List<int[]> hasNullNeighborList = new ArrayList<int[]>();
					hasNullNeighborList.add(new int[]{0, 0});
					
					while(hasNullNeighborList.size() > 0){
						int[] newPassage = makeRandomPassage(hasNullNeighborList);
						generatePassage(newPassage[0], newPassage[1]);
						
						
						hasNullNeighborList = new ArrayList<int[]>();
						
						for(int row=0; row<board.length; row++){
							for(int column=0; column<board[row].length; column++){
								if(board[row][column] != null && hasNullNeighbors(column, row)){
									int[] point = {column, row};
									hasNullNeighborList.add(point);
								}
							}
						}
					}
					
					canvas.repaint();
				}
			}
			
			private int[] makeRandomPassage(List<int[]> hasNullNeighborList) {
				Random rand = new Random();
				int r = rand.nextInt(hasNullNeighborList.size());
				
				int[] passageNearNull = hasNullNeighborList.get(r);
				int x = passageNearNull[0], y = passageNearNull[1];
				
				previous[0] = x;
				previous[1] = y;
				
				Passage currentPassage = board[y][x];
				
				int[] newPassageCoordinates = {0, 0};
				
				if(y > 0 && board[y-1][x] == null){
					currentPassage.setNorthWall(false);
					createNewPassage(x, y-1);
					newPassageCoordinates[0] = x;
					newPassageCoordinates[1] = y-1;
				}
				else if(x < board[0].length-1 && board[y][x+1] == null){
					currentPassage.setEastWall(false);
					createNewPassage(x+1, y);
					newPassageCoordinates[0] = x+1;
					newPassageCoordinates[1] = y;
				}
				else if(y < board.length-1 && board[y+1][x] == null){
					currentPassage.setSouthWall(false);
					createNewPassage(x, y+1);
					newPassageCoordinates[0] = x;
					newPassageCoordinates[1] = y+1;
				}
				else if(x > 0 && board[y][x-1] == null){
					currentPassage.setWestWall(false);
					createNewPassage(x-1, y);
					newPassageCoordinates[0] = x-1;
					newPassageCoordinates[1] = y;
				}
				
				return newPassageCoordinates;
			}

			private void generatePassage(int x, int y) {
				if(lastPassages.size() > 0)
					lastPassages.remove(lastPassages.size()-1);
				
				if(!hasNullNeighbors(x, y) && lastPassages.size() == 0){//x == board[0].length-1 && y == board.length-1){
					System.out.println("STOPPED");
					return;
				}
				
				Passage currentPassage = board[y][x];
				
				
				previous[0] = x;
				previous[1] = y;
				
				System.out.println("Current passage: (" + x + ", " + y + ")");
				if(x > 0 && board[y][x-1] == null){
					currentPassage.setWestWall(randomBool());
					if(!currentPassage.hasWestWall()) createNewPassage(x-1, y);
				}
				if(x < board[0].length-1 && board[y][x+1] == null){
					currentPassage.setEastWall(randomBool());
					if(!currentPassage.hasEastWall()) createNewPassage(x+1, y);
				}
				if(y > 0 && board[y-1][x] == null){
					currentPassage.setNorthWall(randomBool());
					if(!currentPassage.hasNorthWall()) createNewPassage(x, y-1);
				}
				if(y < board.length-1 && board[y+1][x] == null){
					currentPassage.setSouthWall(randomBool());
					if(!currentPassage.hasSouthWall()) createNewPassage(x, y+1);
				}
				
				if(lastPassages.size() == 0){ //Means that we must create at least another 1 passage;
					Random rand = new Random();
					int r = rand.nextInt(4);
					
					boolean foundNewPassage = false;
					while(!foundNewPassage){
						if(r == 0 && y > 0 && board[y-1][x] == null){
							currentPassage.setNorthWall(false);
							createNewPassage(x, y-1);
						}
						else if(r == 1 && x < board[0].length-1 && board[y][x+1] == null){
							currentPassage.setEastWall(false);
							createNewPassage(x+1, y);
						}
						else if(r == 2 && y < board.length-1 && board[y+1][x] == null){
							currentPassage.setSouthWall(false);
							createNewPassage(x, y+1);
						}
						else if(r == 3 && x > 0 && board[y][x-1] == null){
							currentPassage.setWestWall(false);
							createNewPassage(x-1, y);
						}else{
							r++;
							if(r == 4) r = 0;
							continue;
						}
						
						foundNewPassage = true;
					}
				}
				
				
				int[] lastPassage = lastPassages.get(lastPassages.size()-1);
				
				generatePassage(lastPassage[0], lastPassage[1]);
				
			}
			
			private boolean hasNullNeighbors(int x, int y) {
				int[][] neighbors = {{x-1, y}, {x+1, y}, {x, y-1}, {x, y+1}};
				for(int i=0; i<neighbors.length; i++){
					int x1 = neighbors[i][0];
					int y1 = neighbors[i][1];
					
					if(x1 < 0 || x1 > board[0].length-1) continue;
					if(y1 < 0 || y1 > board.length-1) continue;
					
					if(board[y1][x1] == null){
						return true;
					}
				}
				
				return false;
			}

			private boolean randomBool(){
				Random rand = new Random();
				int r = rand.nextInt(10);
				if(r<1){
					return false;
				}
				
				return true;
			}
			
			private void createNewPassage(int x, int y){
				board[y][x] = new Passage();
				Passage currentPassage = board[y][x];
				if(x > 0 && board[y][x-1] != null && (y != previous[1] || x-1 != previous[0])){
					currentPassage.setWestWall(true);
				}
				if(x < board[0].length-1 && board[y][x+1] != null && (y != previous[1] || x+1 != previous[0])){
					currentPassage.setEastWall(true);
				}
				if(y > 0 && board[y-1][x] != null && (y-1 != previous[1] || x != previous[0])){
					currentPassage.setNorthWall(true);
				}
				if(y < board.length-1 && board[y+1][x] != null && (y+1 != previous[1] || x != previous[0])){
					currentPassage.setSouthWall(true);
				}
				System.out.println("Created new passage at: (" + x + ", " + y + ")");
				lastPassages.add(new int[]{x, y});
				
				
			}
		});
		panel.add(buttonGenerateMaze);
		
	}
	
	public class Canvas extends JPanel{

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			int startX = 0, startY = 0;
			
			if(board.length>0){
				startX = (canvas.getWidth() - passageWidth*board[0].length)/2;
				startY = (canvas.getHeight() - passageHeight*board.length)/2;
			}
			
			for(int row=0; row<board.length; row++){
				for(int column=0; column<board[row].length; column++){
					if(board[row][column] != null){
						if(board[row][column].hasEastWall()){
							drawVerticalWall(g, column, row, startX, startY);
						}
						if(board[row][column].hasSouthWall()){
							drawHorizontalWall(g, column, row, startX, startY);
						}
						if(board[row][column].hasWestWall()){
							drawVerticalWall(g, column-1, row, startX, startY);
						}
						if(board[row][column].hasNorthWall()){
							drawHorizontalWall(g, column, row-1, startX, startY);
						}
					}
				}
			}
			
			//Draw a border:
			if(board.length > 0){
				g.drawLine(startX, startY, startX + passageWidth * board[0].length, startY);
				g.drawLine(startX, startY + passageHeight, startX, startY + passageHeight * board.length);	
				g.drawLine(startX, startY + passageHeight * board.length, startX + passageWidth * board[0].length, startY + passageHeight * board.length);	
				g.drawLine(startX + passageWidth * board[0].length, startY + passageHeight * (board.length - 1), startX + passageWidth * board[0].length, startY);	
			}
		}

		private void drawVerticalWall(Graphics g, int x, int y, int startX, int startY) {
			g.drawLine(startX + (x+1) * passageWidth, startY + y * passageHeight, startX + (x+1) * passageWidth, startY + (y+1) * passageHeight);
		}

		private void drawHorizontalWall(Graphics g, int x, int y, int startX, int startY) {
			g.drawLine(startX + x * passageWidth, startY + (y+1) * passageHeight, startX + (x+1) * passageWidth, startY + (y+1) * passageHeight);
		}
		
		
		
	}
}
