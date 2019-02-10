import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

public class editorFrame extends JFrame{
	editorPanel panel;
	int mapID;
	final int scrollWidth=240;
	final int buttonHeight=30;
	final int menuButtonWidth=120;
	JButton deleteSelectedEntityButton;
	public editorFrame(){
		addKeyListener(new myKey());
		addMouseListener(new myMouse());
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				panel.mouseX1 = e.getX();
				panel.mouseY1 = e.getY();
				if(panel.isChangingStartingPosition){
					panel.startingX=e.getX()+panel.scrX;
					panel.startingY=e.getY()+panel.scrY;
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if(panel.isMousePressed){
					int clickX=(e.getX()+panel.scrX)/panel.klX;
					int clickY=(e.getY()+panel.scrY)/panel.klY;
					panel.mouseX2=Math.max(0, Math.min(clickX, panel.matrixWidth-1));
					panel.mouseY2=Math.max(0, Math.min(clickY, panel.matrixHeight-1));
				}
				
			}
		});
		Dimension sSize = Toolkit.getDefaultToolkit ().getScreenSize ();
		setSize (sSize);
		panel = new editorPanel();
		panel.scrW=sSize.width;
		panel.scrH=sSize.height;
		
		
		
		
		setUndecorated(true);
		setTitle("Pasan");
		Container con = getContentPane();
		con.add(panel);
		final JPanel labPanel = new JPanel();
        final JScrollPane scrollPane = new JScrollPane(labPanel);
        labPanel.setLayout(new BoxLayout(labPanel, BoxLayout.Y_AXIS));
		File folder = new File("./mapObjects");
		String[] fileNames = folder.list();
		
		for(int i=0;i<fileNames.length;i++){
			JButton tmp = new JButton(fileNames[i].substring(0, fileNames[i].length()-4));
			tmp.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					panel.curObjectName = tmp.getText();
					panel.updateObjectImage();
					panel.curTileID=-1;
					panel.curEnemyID=-1;
					panel.curNPCID=-1;
					panel.isChangingStartingPosition=false;
					requestFocus();
				}
			});
			tmp.setVisible(true);
			labPanel.add(tmp);
		}
		scrollPane.revalidate();
		scrollPane.setVisible(false);
		scrollPane.setBounds(panel.scrW-scrollWidth, buttonHeight, scrollWidth, panel.scrH);
		panel.add(scrollPane);
		
		final JPanel tilesPanel = new JPanel();
        final JScrollPane tilesPane = new JScrollPane(tilesPanel);
        tilesPanel.setLayout(new BoxLayout(tilesPanel, BoxLayout.Y_AXIS));
		File tilesfolder = new File("./tiles");
		String[] tilesfileNames = tilesfolder.list();
		for(int i=0;i<tilesfileNames.length;i++){
			tilesfileNames[i]=tilesfileNames[i].substring(4);
		}
		
		panel.tileTextures = new Image[tilesfileNames.length];
		
		for(int i=0;i<tilesfileNames.length;i++){
			JButton tmp = new JButton(i+"");
			try{
			panel.tileTextures[i] = ImageIO.read(new File("./tiles/tile"+tilesfileNames[i]));
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			tmp.setIcon(new ImageIcon(panel.tileTextures[i]));
			tmp.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					panel.curTileID = Integer.parseInt(tmp.getText());
					panel.updateTileImage();
					panel.curObjectName=null;
					panel.curEnemyID=-1;
					panel.curNPCID=-1;
					panel.isChangingStartingPosition=false;
					requestFocus();
				}
			});
			tmp.setVisible(true);
			tilesPanel.add(tmp);
		}
		tilesPane.revalidate();
		tilesPane.setVisible(false);
		tilesPane.setBounds(panel.scrW-scrollWidth, buttonHeight, scrollWidth, panel.scrH);
		panel.add(tilesPane);
		
		
		
		
		
		
		
		
		
		
		
		
		final JPanel enemiesPanel = new JPanel();
        final JScrollPane enemiesPane = new JScrollPane(enemiesPanel);
        enemiesPanel.setLayout(new BoxLayout(enemiesPanel, BoxLayout.Y_AXIS));
		File enemiesfolder = new File("./enemies");
		String[] enemiesfileNames = enemiesfolder.list();
		for(int i=0;i<enemiesfileNames.length/8;i++){
			enemiesfileNames[i*8]=enemiesfileNames[i*8].substring(4);
		}
		
		
		for(int i=0;i<enemiesfileNames.length/8;i++){
			JButton tmp = new JButton(i+"");
			tmp.setIcon(new ImageIcon("./enemies/enemy"+panel.addZeros(i)+"6.png"));
			tmp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					panel.curTileID = -1;
					panel.curObjectName=null;
					panel.curEnemyID=Integer.parseInt(tmp.getText());
					panel.curNPCID=-1;
					panel.updateEnemyImage();
					panel.isChangingStartingPosition=false;
					requestFocus();
				}
			});
			tmp.setVisible(true);
			enemiesPanel.add(tmp);
		}
		enemiesPane.revalidate();
		enemiesPane.setVisible(false);
		enemiesPane.setBounds(panel.scrW-scrollWidth, buttonHeight, scrollWidth, panel.scrH);
		panel.add(enemiesPane);
		
		
		
		
		
		
		
		final JPanel NPCsPanel = new JPanel();
        final JScrollPane NPCsPane = new JScrollPane(NPCsPanel);
        NPCsPanel.setLayout(new BoxLayout(NPCsPanel, BoxLayout.Y_AXIS));
		File NPCsfolder = new File("./npcs");
		String[] NPCsfileNames = NPCsfolder.list();
		for(int i=0;i<NPCsfileNames.length/8;i++){
			NPCsfileNames[i*8]=NPCsfileNames[i*8].substring(4);
		}
		
		
		for(int i=0;i<NPCsfileNames.length/8;i++){
			JButton tmp = new JButton(i+"");
			tmp.setIcon(new ImageIcon("./npcs/npc"+panel.addZeros(i)+"6.png"));
			tmp.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					panel.curTileID = -1;
					panel.curObjectName=null;
					panel.curEnemyID=-1;
					panel.curNPCID=Integer.parseInt(tmp.getText());
					panel.updateNPCImage();
					panel.isChangingStartingPosition=false;
					requestFocus();
				}
			});
			tmp.setVisible(true);
			NPCsPanel.add(tmp);
		}
		NPCsPane.revalidate();
		NPCsPane.setVisible(false);
		NPCsPane.setBounds(panel.scrW-scrollWidth, buttonHeight, scrollWidth, panel.scrH);
		panel.add(NPCsPane);
		
		
		
		
		
		
		
		
		
		JButton tilesButton = new JButton("Tiles");
		tilesButton.setBounds(panel.scrW-scrollWidth*2, 0, scrollWidth/2, buttonHeight);
		tilesButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!tilesPane.isVisible()){
					scrollPane.setVisible(false);
					enemiesPane.setVisible(false);
					NPCsPane.setVisible(false);
					tilesPane.setVisible(true);
				}
				else{
					tilesPane.setVisible(false);
				}
				requestFocus();
			}
		});
		tilesButton.setVisible(true);
		panel.add(tilesButton);

		JButton objectsButton = new JButton("Objects");
		objectsButton.setBounds(panel.scrW-scrollWidth*3/2, 0, scrollWidth/2, buttonHeight);
		objectsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!scrollPane.isVisible()){
					tilesPane.setVisible(false);
					enemiesPane.setVisible(false);
					NPCsPane.setVisible(false);
					scrollPane.setVisible(true);
				}
				else{
					scrollPane.setVisible(false);
				}
				requestFocus();
			}
		});
		objectsButton.setVisible(true);
		panel.add(objectsButton);
		
		JButton enemiesButton = new JButton("Enemies");
		enemiesButton.setBounds(panel.scrW-scrollWidth, 0, scrollWidth/2, buttonHeight);
		enemiesButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!enemiesPane.isVisible()){
					tilesPane.setVisible(false);
					NPCsPane.setVisible(false);
					scrollPane.setVisible(false);
					enemiesPane.setVisible(true);
				}
				else{
					enemiesPane.setVisible(false);
				}
				requestFocus();
			}
		});
		enemiesButton.setVisible(true);
		panel.add(enemiesButton);
		
		JButton npcsButton = new JButton("NPCs");
		npcsButton.setBounds(panel.scrW-scrollWidth/2, 0, scrollWidth/2, buttonHeight);
		npcsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!NPCsPane.isVisible()){
					tilesPane.setVisible(false);
					scrollPane.setVisible(false);
					enemiesPane.setVisible(false);
					NPCsPane.setVisible(true);
				}
				else{
					NPCsPane.setVisible(false);
				}
				requestFocus();
			}
		});
		npcsButton.setVisible(true);
		panel.add(npcsButton);
		
		JButton newMapButton = new JButton("New map");
		newMapButton.setBounds(0, 0, menuButtonWidth, buttonHeight);
		newMapButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(null, "Create a new map?", "New map", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
					createNewMap();
					requestFocus();
				}
			}
		});
		newMapButton.setVisible(true);
		panel.add(newMapButton);

		JButton saveMapButton = new JButton("Save");
		saveMapButton.setBounds(menuButtonWidth, 0, menuButtonWidth, buttonHeight);
		saveMapButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveMap();
				requestFocus();
			}
		});
		saveMapButton.setVisible(true);
		panel.add(saveMapButton);

		JButton loadMapButton = new JButton("Open map");
		loadMapButton.setBounds(menuButtonWidth*2, 0, menuButtonWidth, buttonHeight);
		loadMapButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openMap(false);
				requestFocus();
			}
		});
		loadMapButton.setVisible(true);
		panel.add(loadMapButton);
		
		JButton showGridButton = new JButton("Show grid");
		showGridButton.setBounds(menuButtonWidth*3, 0, menuButtonWidth, buttonHeight);
		showGridButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.showGrid=!panel.showGrid;
				requestFocus();
			}
		});
		showGridButton.setVisible(true);
		panel.add(showGridButton);
		
		JButton showPassabilityButton = new JButton("Passability");
		showPassabilityButton.setBounds(menuButtonWidth*4, 0, menuButtonWidth, buttonHeight);
		showPassabilityButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.showPassability=!panel.showPassability;
				requestFocus();
			}
		});
		showPassabilityButton.setVisible(true);
		panel.add(showPassabilityButton);
		
		JButton changeBackgrouldColorButton = new JButton("Background");
		changeBackgrouldColorButton.setBounds(menuButtonWidth*5, 0, menuButtonWidth, buttonHeight);
		changeBackgrouldColorButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Scanner sc = new Scanner(JOptionPane.showInputDialog(null, "Input three integers in range 0 to 255 - RGB code of the new background color.", "Change background color", JOptionPane.PLAIN_MESSAGE));
				try{
					Color c = new Color(sc.nextInt(), sc.nextInt(), sc.nextInt());
					panel.backgroundColor=c;
				}
				catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Uncorrect input", "Error", JOptionPane.ERROR_MESSAGE);
				}
				requestFocus();
			}
		});
		changeBackgrouldColorButton.setVisible(true);
		panel.add(changeBackgrouldColorButton);
		
		JButton changeStartingPositionButton = new JButton("Player");
		changeStartingPositionButton.setBounds(menuButtonWidth*6, 0, menuButtonWidth, buttonHeight);
		changeStartingPositionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.isChangingStartingPosition=true;
				panel.curTileID=-1;
				panel.curObjectName=null;
				
				requestFocus();
			}
		});
		changeStartingPositionButton.setVisible(true);
		panel.add(changeStartingPositionButton);
		
		JButton changeMapSizeButton = new JButton("Map size");
		changeMapSizeButton.setBounds(menuButtonWidth*7, 0, menuButtonWidth, buttonHeight);
		changeMapSizeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					int width = Math.abs(Integer.parseInt(JOptionPane.showInputDialog(null, "Input the map's new width (in tiles "+panel.klX+"x"+panel.klY+"px)", "Change map size", JOptionPane.PLAIN_MESSAGE).trim()));
					int height = Math.abs(Integer.parseInt(JOptionPane.showInputDialog(null, "Input the map's new height (in tiles "+panel.klX+"x"+panel.klY+"px)", "Change map size", JOptionPane.PLAIN_MESSAGE).trim()));
					byte[][] newPassabilityMatrix = new byte[width][height];
					int[][] newTileMatrix = new int[width][height];
					for(int i=0;i<Math.min(width, panel.matrixWidth);i++){
						for(int j=0;j<Math.min(height, panel.matrixHeight);j++){
							newPassabilityMatrix[i][j]=panel.matrix[i][j];
							newTileMatrix[i][j]=panel.tileMatrix[i][j];
						}
					}
					if(width>panel.matrixWidth){
						for(int i=panel.matrixWidth;i<width;i++){
							for(int j=0;j<height;j++){
								newPassabilityMatrix[i][j]=0;
								newTileMatrix[i][j]=-1;
							}
						}
					}
					if(height>panel.matrixHeight){
						for(int i=0;i<width;i++){
							for(int j=panel.matrixHeight;j<height;j++){
								newPassabilityMatrix[i][j]=0;
								newTileMatrix[i][j]=-1;
							}
						}
					}
					for(int i=0;i<panel.mapObjects.size();i++){
						MapObject tmp = panel.mapObjects.get(i);
						if(tmp.x>=width*panel.klX | tmp.y>=height*panel.klY){
							panel.mapObjects.remove(i);
							i--;
						}
					}
					for(int i=0;i<panel.enemies.size();i++){
						Enemy tmp = panel.enemies.get(i);
						if(tmp.x>=width*panel.klX | tmp.y>=height*panel.klY){
							panel.enemies.remove(i);
							i--;
						}
					}
					for(int i=0;i<panel.npcs.size();i++){
						NPC tmp = panel.npcs.get(i);
						if(tmp.x>=width*panel.klX | tmp.y>=height*panel.klY){
							panel.npcs.remove(i);
							i--;
						}
					}
					if(panel.startingX>=width*panel.klX | panel.startingY>=height*panel.klY){
						panel.startingX=64;
						panel.startingY=116;
					}
					panel.matrixWidth=width;
					panel.matrixHeight=height;
					panel.matrix=newPassabilityMatrix;
					panel.tileMatrix=newTileMatrix;
				}
				catch(Exception ex){
					JOptionPane.showMessageDialog(null, "Uncorrect input", "Error", JOptionPane.ERROR_MESSAGE);
				}
				requestFocus();
			}
		});
		changeMapSizeButton.setVisible(true);
		panel.add(changeMapSizeButton);
		
		
		deleteSelectedEntityButton = new JButton("Delete object");
		deleteSelectedEntityButton.setBounds(menuButtonWidth*8, 0, menuButtonWidth, buttonHeight);
		deleteSelectedEntityButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(panel.selectedEnemyID!=-1){
					panel.enemies.remove(panel.selectedEnemyID);
					panel.selectedEnemyID=-1;
				}
				else if(panel.selectedNPCID!=-1){
					panel.npcs.remove(panel.selectedNPCID);
					panel.selectedNPCID=-1;
				}
				else if(panel.selectedObjectID!=-1){
					panel.mapObjects.remove(panel.selectedObjectID);
					panel.selectedObjectID=-1;
				}
				deleteSelectedEntityButton.setVisible(false);
				requestFocus();
			}
		});
		deleteSelectedEntityButton.setVisible(false);
		panel.add(deleteSelectedEntityButton);
		
		JButton exitBtn = new JButton("Exit");
		exitBtn.setBounds(0, panel.scrH-buttonHeight, menuButtonWidth, buttonHeight);
		exitBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(JOptionPane.showConfirmDialog(null, "Do you want to close editor?\nAll unsaved changes will be lost.", "Exit", JOptionPane.YES_NO_OPTION)==JOptionPane.OK_OPTION){
					System.exit(0);
				}
			}
		});
		exitBtn.setVisible(true);
		panel.add(exitBtn);
		
		setVisible(true);
		
		int ans = JOptionPane.showOptionDialog(null, "Welcome to the Shattered World map editor!", "Map editor", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Create a new map","Open map","Exit"}, null);
		if(ans==JOptionPane.YES_OPTION){
			createNewMap();
		}
		else if(ans==JOptionPane.NO_OPTION){
			openMap(true);
		}
		else if(ans==JOptionPane.CANCEL_OPTION){
			System.exit(0);
		}
		panel.timerDraw.start();
		requestFocus();
	}
	public void createNewMap(){
		File mapsfolder = new File("./maps");
		String[] mapNames = mapsfolder.list();
		int width = Integer.parseInt(JOptionPane.showInputDialog(null, "Input the new map's width (in tiles "+panel.klX+"x"+panel.klY+"px)", "New map", JOptionPane.PLAIN_MESSAGE).trim());
		int height = Integer.parseInt(JOptionPane.showInputDialog(null, "Input the new map's height (in tiles "+panel.klX+"x"+panel.klY+"px)", "New map", JOptionPane.PLAIN_MESSAGE).trim());
		panel.matrixWidth=width;
		panel.matrixHeight=height;
		panel.startingX=64;
		panel.startingY=116;
		panel.moveDirection=6;
		panel.updatePlayerImage();
		
		panel.matrix = new byte[panel.matrixWidth][panel.matrixHeight];
		for(int i=0; i<panel.matrixWidth; i++){
			for(int j=0; j<panel.matrixHeight; j++){
				panel.matrix[i][j]=0;
			}
		}
				
		panel.tileMatrix = new int[panel.matrixWidth][panel.matrixHeight];
		for(int i=0; i<panel.matrixWidth; i++){
			for(int j=0; j<panel.matrixHeight; j++){
				panel.tileMatrix[i][j]=-1;
			}
		}
		
		mapID = mapNames.length;
		
		File file = new File("./maps/"+mapID+".swmap");
		try{
			FileWriter writer = new FileWriter(file);
			writer.write("255 255 255 "+width+" "+height+" ");
			for(int i=0;i<width*height;i++){
				writer.write("-1 ");
			}
			for(int i=0;i<width*height;i++){
				writer.write("0 ");
			}
			writer.write("0 0 0 64 116 6");
			writer.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		file = new File("./MapConsts.java");
		try{
			String str = new String(Files.readAllBytes(file.toPath()));
			String lineSeparator = System.getProperty("line.separator");
			str = str.replaceFirst("//mapWidth"+mapID, "//mapWidth"+mapID+lineSeparator+"case "+mapID+": return "+width+";"+lineSeparator+"//mapWidth"+(mapID+1));
			
			str = str.replaceFirst("//mapHeight"+mapID, "//mapHeight"+mapID+lineSeparator+"case "+mapID+": return "+height+";"+lineSeparator+"//mapHeight"+(mapID+1));
			
			str = str.replaceFirst("//tilesTexturesArray"+mapID, "//tilesTexturesArray"+mapID+lineSeparator+"case "+mapID+": return new Bitmap[]{};"+lineSeparator+"//tilesTexturesArray"+(mapID+1));
			
			String tmp="case "+mapID+": return new int[][]{"+lineSeparator;
			for(int i=0;i<width-1;i++){
				tmp+="{";
				for(int j=0;j<height-1;j++){
					tmp+="-1, ";
				}
				tmp+="-1},"+lineSeparator;
			}
			tmp+="{";
			for(int j=0;j<height-1;j++){
				tmp+="-1, ";
			}
			tmp+="-1}"+lineSeparator;
			tmp+="};"+lineSeparator+"//tileMatrix"+(mapID+1);

			str = str.replaceFirst("//tileMatrix"+mapID, "//tileMatrix"+mapID+lineSeparator+tmp);
			
			tmp="case "+mapID+": return new byte[][]{"+lineSeparator;
			for(int i=0;i<width-1;i++){
				tmp+="{";
				for(int j=0;j<height-1;j++){
					tmp+="0, ";
				}
				tmp+="0},"+lineSeparator;
				
			}
			tmp+="{";
			for(int j=0;j<height-1;j++){
				tmp+="0, ";
			}
			tmp+="0}"+lineSeparator;
			tmp+="};"+lineSeparator+"//passabilityMatrix"+(mapID+1);
			str = str.replaceFirst("//passabilityMatrix"+mapID, "//passabilityMatrix"+mapID+lineSeparator+tmp);
			
			str = str.replaceFirst("//mapObjectsTexturesArray"+mapID, "//mapObjectsTexturesArray"+mapID+lineSeparator+"case "+mapID+": return new Bitmap[]{};"+lineSeparator+"//mapObjectsTexturesArray"+(mapID+1));

			str = str.replaceFirst("//mapObjectsArray"+mapID, "//mapObjectsArray"+mapID+lineSeparator+"case "+mapID+": return new SingleGameMapObject[]{};"+lineSeparator+"//mapObjectsArray"+(mapID+1));

			str = str.replaceFirst("//enemiesArray"+mapID, "//enemiesArray"+mapID+lineSeparator+"case "+mapID+": return new SingleGameEnemy[]{};"+lineSeparator+"//enemiesArray"+(mapID+1));
			
			str = str.replaceFirst("//NPCsArray"+mapID, "//NPCsArray"+mapID+lineSeparator+"case "+mapID+": return new SingleGameNPC[]{};"+lineSeparator+"//NPCsArray"+(mapID+1));

			str = str.replaceFirst("//startingX"+mapID, "//startingX"+mapID+lineSeparator+"case "+mapID+": return 64;"+lineSeparator+"//startingX"+(mapID+1));

			str = str.replaceFirst("//startingY"+mapID, "//startingY"+mapID+lineSeparator+"case "+mapID+": return 116;"+lineSeparator+"//startingY"+(mapID+1));

			str = str.replaceFirst("//moveDirection"+mapID, "//moveDirection"+mapID+lineSeparator+"case "+mapID+": return 6;"+lineSeparator+"//moveDirection"+(mapID+1));

			str = str.replaceFirst("//backgroundColor"+mapID, "//backgroundColor"+mapID+lineSeparator+"case "+mapID+": {"+lineSeparator+"Paint p = new Paint();"+lineSeparator+"p.setARGB(255,255,255,255);"+lineSeparator+"return p;}"+lineSeparator+"//backgroundColor"+(mapID+1));
			FileWriter out = new FileWriter(file);
			out.write(str);
			out.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	public void openMap(boolean isFirstLaunch){
			JFileChooser fc = new JFileChooser("./maps");
			fc.addChoosableFileFilter(new FileFilter() {
				
				@Override
				public String getDescription() {
					return "Shattered World maps (.SWMAP)";
				}
				
				@Override
				public boolean accept(File f) {
					if(f.getName().endsWith(".swmap")) return true;
					else return false;
				}
			});
			fc.setDialogTitle("Open map");
			
			if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				if(file.getName().endsWith(".swmap")){
				try{
					mapID = Integer.parseInt(file.getName().substring(0, file.getName().length()-6));
					Scanner in = new Scanner(file);
					panel.backgroundColor = new Color(in.nextInt(), in.nextInt(), in.nextInt());
					panel.matrixWidth = in.nextInt();
					panel.matrixHeight = in.nextInt();
					panel.tileMatrix = new int[panel.matrixWidth][panel.matrixHeight];
					for(int i=0;i<panel.matrixWidth;i++){
						for(int j=0;j<panel.matrixHeight;j++){
							panel.tileMatrix[i][j]=in.nextInt();
						}
					}
					
					panel.matrix = new byte[panel.matrixWidth][panel.matrixHeight];
					for(int i=0;i<panel.matrixWidth;i++){
						for(int j=0;j<panel.matrixHeight;j++){
							panel.matrix[i][j]=in.nextByte();
						}
					}
					
					int kol = in.nextInt();
					panel.mapObjects.clear();
					for(int i=0;i<kol;i++){
						panel.mapObjects.add(new MapObject(in.nextInt(), in.nextInt(), in.next()));
					}
					
					kol = in.nextInt();
					panel.enemies.clear();
					for(int i=0;i<kol;i++){
						panel.enemies.add(new Enemy(in.nextInt(), in.nextInt(), in.nextInt(), in.nextByte()));
					}
					
					kol = in.nextInt();
					panel.npcs.clear();
					for(int i=0;i<kol;i++){
						panel.npcs.add(new NPC(in.nextInt(), in.nextInt(), in.nextInt(), in.nextByte()));
					}
					
					panel.startingX = in.nextInt();
					panel.startingY = in.nextInt();
					panel.moveDirection = in.nextByte();
					panel.updatePlayerImage();
					in.close();
					requestFocus();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				}
				else{
					JOptionPane.showMessageDialog(null, "Map file's name must end with .swmap", "Uncorrect file", JOptionPane.ERROR_MESSAGE);
					if(isFirstLaunch){
						int ans = JOptionPane.showOptionDialog(null, "Welcome to the Shattered World map editor!", "Map editor", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Create a new map","Open map","Exit"}, null);
						if(ans==JOptionPane.YES_OPTION){
							createNewMap();
						}
						else if(ans==JOptionPane.NO_OPTION){
							openMap(true);
						}
						else if(ans==JOptionPane.CANCEL_OPTION){
							System.exit(0);
						}
					}
				}
				
			}
			else if(isFirstLaunch){
				int ans = JOptionPane.showOptionDialog(null, "Welcome to the Shattered World map editor!", "Map editor", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Create a new map","Open map","Exit"}, null);
				if(ans==JOptionPane.YES_OPTION){
					createNewMap();
				}
				else if(ans==JOptionPane.NO_OPTION){
					openMap(true);
				}
				else if(ans==JOptionPane.CANCEL_OPTION){
					System.exit(0);
				}
			}
	}
	public void saveMap(){
		File file = new File("./maps/"+mapID+".swmap");
		try{
			FileWriter writer = new FileWriter(file);
			writer.write(panel.backgroundColor.getRed()+" "+panel.backgroundColor.getGreen()+" "+panel.backgroundColor.getBlue()+" "+panel.matrixWidth+" "+panel.matrixHeight+" ");
			for(int i=0;i<panel.matrixWidth;i++){
				for(int j=0;j<panel.matrixHeight;j++){
					writer.write(panel.tileMatrix[i][j]+" ");
				}
			}
			for(int i=0;i<panel.matrixWidth;i++){
				for(int j=0;j<panel.matrixHeight;j++){
					writer.write(panel.matrix[i][j]+" ");
				}
			}
			writer.write(panel.mapObjects.size()+" ");
			for(int i=0;i<panel.mapObjects.size();i++){
				MapObject obj = panel.mapObjects.get(i);
				writer.write(obj.x+" "+obj.y+" "+obj.textureName+" ");
			}
			writer.write(panel.enemies.size()+" ");
			for(int i=0;i<panel.enemies.size();i++){
				Enemy en = panel.enemies.get(i);
				writer.write(en.x+" "+en.y+" "+en.textureID+" "+en.moveDirection+" ");
			}
			writer.write(panel.npcs.size()+" ");
			for(int i=0;i<panel.npcs.size();i++){
				NPC npc = panel.npcs.get(i);
				writer.write(npc.x+" "+npc.y+" "+npc.textureID+" "+npc.moveDirection+" ");
			}
			writer.write(panel.startingX+" "+panel.startingY+" "+panel.moveDirection);
			writer.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		file = new File("./MapConsts.java");
		try{
			String str = new String(Files.readAllBytes(file.toPath()));
			String lineSeparator = System.getProperty("line.separator");
			
			str = replace(str,"mapWidth","case "+mapID+": return "+panel.matrixWidth+";");
			
			str = replace(str,"mapHeight","case "+mapID+": return "+panel.matrixHeight+";");
			
			ArrayList<Integer> tiles = new ArrayList<Integer>();
			for(int i=0;i<panel.matrixWidth;i++){
				for(int j=0;j<panel.matrixHeight;j++){
					int id = panel.tileMatrix[i][j];
					if(id!=-1){
						if(!tiles.contains(id)){
							tiles.add(id);
						}
					}
				}
			}
			String tmp="case "+mapID+": return new Bitmap[]{"+lineSeparator;
			for(int i=0;i<tiles.size();i++){
				tmp+="BitmapFactory.decodeResource(res, R.drawable.tile"+tiles.get(i)+")";
				if(i<tiles.size()-1){
					tmp+=","+lineSeparator;
				}
			}
			tmp+="};";
			str=replace(str,"tilesTexturesArray",tmp);
			
			
			tmp="case "+mapID+": return new int[][]{"+lineSeparator;
			for(int i=0;i<panel.matrixWidth;i++){
				tmp+="{";
				for(int j=0;j<panel.matrixHeight-1;j++){
					if(panel.tileMatrix[i][j]==-1){
						tmp+="-1, ";
					}
					else{
						tmp+=tiles.indexOf(panel.tileMatrix[i][j])+", ";
					}
				}
				if(panel.tileMatrix[i][panel.matrixHeight-1]==-1){
					tmp+="-1}";
				}
				else{
					tmp+=tiles.indexOf(panel.tileMatrix[i][panel.matrixHeight-1])+"}";
				}
				if(i<panel.matrixWidth-1){
					tmp+=","+lineSeparator;
				}
			}
			tmp+=lineSeparator+"};";
			
			str = replace(str,"tileMatrix",tmp);
			
			tmp="case "+mapID+": return new byte[][]{"+lineSeparator;
			for(int i=0;i<panel.matrixWidth;i++){
				tmp+="{";
				for(int j=0;j<panel.matrixHeight;j++){
					tmp+=panel.matrix[i][j];
					if(j<panel.matrixHeight-1){
						tmp+=", ";
					}
					else{
						tmp+="}";
					}
				}
				if(i<panel.matrixWidth-1){
					tmp+=","+lineSeparator;
				}
			}
			tmp+=lineSeparator+"};";
			str = replace(str,"passabilityMatrix",tmp);
			
			ArrayList<String> textures = new ArrayList<String>();
			for(int i=0;i<panel.mapObjects.size();i++){
				String name = panel.mapObjects.get(i).textureName;
				if(!textures.contains(name)){
					textures.add(name);
				}
			}
			tmp="case "+mapID+": return new Bitmap[]{"+lineSeparator;
			for(int i=0;i<textures.size();i++){
				tmp+="Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, R.drawable."+textures.get(i)+"), "+getObjectWidth(textures.get(i))+"*GameConsts.scrH/960, "+getObjectHeight(textures.get(i))+"*GameConsts.scrH/960, false)";
				if(i<textures.size()-1){
					tmp+=","+lineSeparator;
				}
			}
			tmp+="};";
			str=replace(str,"mapObjectsTexturesArray",tmp);
			
			tmp="case "+mapID+": return new SingleGameMapObject[]{"+lineSeparator;
			for(int i=0;i<panel.mapObjects.size();i++){
				MapObject obj=panel.mapObjects.get(i);
				tmp+="new SingleGameMapObject("+obj.x+", "+obj.y+", "+textures.indexOf(obj.textureName)+")";
				if(i<panel.mapObjects.size()-1){
					tmp+=","+lineSeparator;
				}
			}
			tmp+="};";
			str=replace(str, "mapObjectsArray", tmp);
			
			tmp="case "+mapID+": return new SingleGameEnemy[]{"+lineSeparator;
			for(int i=0;i<panel.enemies.size();i++){
				Enemy en=panel.enemies.get(i);
				tmp+="new SingleGameEnemy("+en.x+", "+en.y+", "+en.textureID+", "+en.moveDirection+")";
				if(i<panel.enemies.size()-1){
					tmp+=","+lineSeparator;
				}
			}
			tmp+="};";
			str=replace(str, "enemiesArray", tmp);
			
			tmp="case "+mapID+": return new SingleGameNPC[]{"+lineSeparator;
			for(int i=0;i<panel.npcs.size();i++){
				NPC npc=panel.npcs.get(i);
				tmp+="new SingleGameNPC("+npc.x+", "+npc.y+", "+npc.textureID+", "+npc.moveDirection+")";
				if(i==panel.npcs.size()-1){
					tmp+=","+lineSeparator;
				}
			}
			tmp+="};";
			str=replace(str, "NPCsArray", tmp);
			
			str=replace(str, "startingX", "case "+mapID+": return "+panel.startingX+";");

			str=replace(str, "startingY", "case "+mapID+": return "+panel.startingY+";");

			str=replace(str, "moveDirection", "case "+mapID+": return "+panel.moveDirection+";");
			
			str=replace(str, "backgroundColor", "case "+mapID+":{"+lineSeparator+"Paint p = new Paint();"+lineSeparator+"p.setARGB(255,"+panel.backgroundColor.getRed()+","+panel.backgroundColor.getGreen()+","+panel.backgroundColor.getBlue()+");"+lineSeparator+"return p;}");
			
			
			FileWriter out = new FileWriter(file);
			out.write(str);
			out.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public int getObjectWidth(String name){
		try{
			return ImageIO.read(new File("./mapObjects/"+name+".png")).getWidth();
		}
		catch(Exception ex){
			return 0;
		}
	}
	public int getObjectHeight(String name){
		try{
			return ImageIO.read(new File("./mapObjects/"+name+".png")).getHeight();
		}
		catch(Exception ex){
			return 0;
		}
	}
	public String replace(String src, String param, String replacement){
		String lineSeparator = System.getProperty("line.separator");
		String str = "//"+param+mapID;
		int ind1 = src.indexOf(str)+str.length();
		int ind2 = src.indexOf("//"+param+(mapID+1));
		return src.substring(0, ind1)+lineSeparator+replacement+lineSeparator+src.substring(ind2);
	}
	public class myKey implements KeyListener{

		@Override
		public void keyPressed(KeyEvent e) {
			int key_=e.getKeyCode();
			if(key_==KeyEvent.VK_UP | key_==KeyEvent.VK_W){
				panel.scrY-=panel.klX;
			}
			else if(key_==KeyEvent.VK_DOWN | key_==KeyEvent.VK_S){
				panel.scrY+=panel.klX;
			}
			else if(key_==KeyEvent.VK_LEFT | key_==KeyEvent.VK_A){
				panel.scrX-=panel.klY;
			}
			else if(key_==KeyEvent.VK_RIGHT | key_==KeyEvent.VK_D){
				panel.scrX+=panel.klY;
			}
			else if(key_==KeyEvent.VK_DELETE){
				if(deleteSelectedEntityButton.isVisible()){
					if(panel.selectedEnemyID!=-1){
						panel.enemies.remove(panel.selectedEnemyID);
						panel.selectedEnemyID=-1;
					}
					else if(panel.selectedNPCID!=-1){
						panel.npcs.remove(panel.selectedNPCID);
						panel.selectedNPCID=-1;
					}
					else if(panel.selectedObjectID!=-1){
						panel.mapObjects.remove(panel.selectedObjectID);
						panel.selectedObjectID=-1;
					}
					deleteSelectedEntityButton.setVisible(false);
				}
				else if(!panel.isMousePressed){
					panel.tileMatrix[Math.max(0, Math.min((panel.mouseX1+panel.scrX)/panel.klX, panel.matrixWidth-1))][Math.max(0, Math.min((panel.mouseY1+panel.scrY)/panel.klY, panel.matrixHeight-1))]=-1;
				}
			}
			else if(key_==KeyEvent.VK_ESCAPE){
				panel.curObjectName=null;
				panel.curTileID=-1;
				panel.curEnemyID=-1;
				panel.curNPCID=-1;
				panel.selectedEnemyID=-1;
				panel.selectedNPCID=-1;
				panel.selectedObjectID=-1;
				deleteSelectedEntityButton.setVisible(false);
				panel.isChangingStartingPosition=false;
				panel.isMousePressed=false;
				
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			
		}
		
	}
	public class myMouse implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
				int clickX=(e.getX()+panel.scrX)/panel.klX;
				int clickY=(e.getY()+panel.scrY)/panel.klY;
				if((clickX>=0)&&(clickX<panel.matrixWidth)&&(clickY>=0)&&(clickY<panel.matrixHeight)){
					if(panel.isChangingStartingPosition){
						panel.isChangingStartingPosition=false;
						panel.startingX=e.getX()+panel.scrX;
						panel.startingY=e.getY()+panel.scrY;
						try{
							int m = Integer.parseInt(JOptionPane.showInputDialog(null, "Input an integer in range 0 to 7 - player's starting move position.\n0 means right, 1 means up-right, 2 means up etc.", "Choose player's starting move position", JOptionPane.PLAIN_MESSAGE).trim());
							if(m>=0 && m<=7){
								panel.moveDirection=(byte)m;
							}
							else{
								JOptionPane.showMessageDialog(null, "Uncorrect input", "Error", JOptionPane.ERROR_MESSAGE);
								panel.moveDirection=6;
							}
						}
						catch(Exception ex){
							JOptionPane.showMessageDialog(null, "Uncorrect input", "Error", JOptionPane.ERROR_MESSAGE);
							panel.moveDirection=6;
						}
						panel.updatePlayerImage();
					}
					else if(panel.curTileID!=-1){
						panel.tileMatrix[clickX][clickY]=panel.curTileID;
					}
					else if(panel.curObjectName!=null){
						panel.mapObjects.add(new MapObject(e.getX()+panel.scrX, e.getY()+panel.scrY, panel.curObjectName));
					}
					else if(panel.curEnemyID!=-1){
						try{
							byte m = (byte)Integer.parseInt(JOptionPane.showInputDialog(null, "Input an integer in range 0 to 7 - enemy's starting move position.\n0 means right, 1 means up-right, 2 means up etc.", "Choose enemy's starting move position", JOptionPane.PLAIN_MESSAGE).trim());
							if(m>=0 && m<=7){
								panel.enemies.add(new Enemy(e.getX()+panel.scrX, e.getY()+panel.scrY, panel.curEnemyID, m));
							}
							else{
								JOptionPane.showMessageDialog(null, "Uncorrect input", "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
						catch(Exception ex){
							JOptionPane.showMessageDialog(null, "Uncorrect input", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					else if(panel.curNPCID!=-1){
						try{
							byte m = (byte)Integer.parseInt(JOptionPane.showInputDialog(null, "Input an integer in range 0 to 7 - NPC's starting move position.\n0 means right, 1 means up-right, 2 means up etc.", "Choose NPC's starting move position", JOptionPane.PLAIN_MESSAGE).trim());
							if(m>=0 && m<=7){
								panel.npcs.add(new NPC(e.getX()+panel.scrX, e.getY()+panel.scrY, panel.curNPCID, m));
							}
							else{
								JOptionPane.showMessageDialog(null, "Uncorrect input", "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
						catch(Exception ex){
							JOptionPane.showMessageDialog(null, "Uncorrect input", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					else if(panel.showPassability){
						try{
							if(e.getButton()==MouseEvent.BUTTON1){
								panel.matrix[clickX][clickY]+=1;
								if(panel.matrix[clickX][clickY]==3){
									panel.matrix[clickX][clickY]=0;
								}
							}
							else if(e.getButton()==MouseEvent.BUTTON3){
								panel.matrix[clickX][clickY]-=1;
								if(panel.matrix[clickX][clickY]==-1){
									panel.matrix[clickX][clickY]=2;
								}
							}
						}
						catch(Exception ex){
							ex.printStackTrace();
						}
					}
					else{
						for(int i=0;i<panel.enemies.size();i++){
							Enemy en = panel.enemies.get(i);
							if((e.getX()+panel.scrX>en.x)&&(e.getX()+panel.scrX<en.x+en.image.getWidth(null))&&(e.getY()+panel.scrY>en.y)&&(e.getY()+panel.scrY<en.y+en.image.getHeight(null))){
								panel.selectedEnemyID=i;
								panel.selectedNPCID=-1;
								panel.selectedObjectID=-1;
								deleteSelectedEntityButton.setVisible(true);
								break;
							}
						}
						if(panel.selectedEnemyID==-1){
							for(int i=0;i<panel.npcs.size();i++){
								NPC npc = panel.npcs.get(i);
								if((e.getX()+panel.scrX>npc.x)&&(e.getX()+panel.scrX<npc.x+npc.image.getWidth(null))&&(e.getY()+panel.scrY>npc.y)&&(e.getY()+panel.scrY<npc.y+npc.image.getHeight(null))){
									panel.selectedNPCID=i;
									panel.selectedObjectID=-1;
									deleteSelectedEntityButton.setVisible(true);
									break;
								}
							}
							if(panel.selectedNPCID==-1){
								for(int i=0;i<panel.mapObjects.size();i++){
									MapObject obj = panel.mapObjects.get(i);
									if((e.getX()+panel.scrX>obj.x)&&(e.getX()+panel.scrX<obj.x+obj.width)&&(e.getY()+panel.scrY>obj.y)&&(e.getY()+panel.scrY<obj.y+obj.height)){
										panel.selectedObjectID=i;
										deleteSelectedEntityButton.setVisible(true);
										break;
									}
								}
							}
						}
					}
				}
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if(panel.curTileID!=-1){
				int clickX=(e.getX()+panel.scrX)/panel.klX;
				int clickY=(e.getY()+panel.scrY)/panel.klY;
				if((clickX>=0)&&(clickX<panel.matrixWidth)&&(clickY>=0)&&(clickY<panel.matrixHeight)){
					panel.mouseX1=clickX;
					panel.mouseY1=clickY;
					panel.mouseX2=clickX;
					panel.mouseY2=clickY;
					panel.isMousePressed=true;
				}
			}
			else if(panel.showPassability){
				int clickX=(e.getX()+panel.scrX)/panel.klX;
				int clickY=(e.getY()+panel.scrY)/panel.klY;
				if((clickX>=0)&&(clickX<panel.matrixWidth)&&(clickY>=0)&&(clickY<panel.matrixHeight)){
					panel.mouseX1=clickX;
					panel.mouseY1=clickY;
					panel.mouseX2=clickX;
					panel.mouseY2=clickY;
					if(e.getButton()==MouseEvent.BUTTON1){
						
						panel.curPassability=(byte)(panel.matrix[clickX][clickY]+1);
						if(panel.curPassability==3){
							panel.curPassability=0;
						}
					}
					else if(e.getButton()==MouseEvent.BUTTON3){
						panel.curPassability=(byte)(panel.matrix[clickX][clickY]-1);
						if(panel.curPassability==-1){
							panel.curPassability=2;
						}
					}
					panel.isMousePressed=true;
				}
			}
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if(panel.isMousePressed){
				panel.isMousePressed=false;
				if(panel.curTileID!=-1){
					for(int i=Math.min(panel.mouseX1,panel.mouseX2);i<=Math.max(panel.mouseX1,panel.mouseX2);i++){
						for(int j=Math.min(panel.mouseY1,panel.mouseY2);j<=Math.max(panel.mouseY1,panel.mouseY2);j++){
							panel.tileMatrix[i][j]=panel.curTileID;
						}
					}
				}
				else if(panel.showPassability){
					for(int i=Math.min(panel.mouseX1,panel.mouseX2);i<=Math.max(panel.mouseX1,panel.mouseX2);i++){
						for(int j=Math.min(panel.mouseY1,panel.mouseY2);j<=Math.max(panel.mouseY1,panel.mouseY2);j++){
							panel.matrix[i][j]=panel.curPassability;
						}
					}
				}
				panel.mouseX1=-1;
				panel.mouseY1=-1;
			}
		}
		
	}
}
