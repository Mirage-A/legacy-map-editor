import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class editorPanel extends JPanel{
	int scrW, scrH;
	final int klX=24, klY=24;
	Image road, wall, grass;
	int scrX=0, scrY=0;
	Timer timerDraw;
	String curObjectName=null;
	Image curObjectImage,curEnemyImage,curNPCImage;
	int curTileID=-1,curEnemyID=-1,curNPCID=-1;
	Image curTileImage;
	int mouseX1=-1,mouseY1=-1,mouseX2=-1,mouseY2=-1;
	
	int selectedObjectID=-1,selectedEnemyID=-1,selectedNPCID=-1;
	byte curPassability=-1;
	boolean isMousePressed=false;
	boolean showGrid=true;
	boolean showPassability=false;
	boolean isChangingStartingPosition=false;
	
	Image[] tileTextures;
	Image playerTexture;
	
	byte[][] matrix;
	int[][] tileMatrix;
	ArrayList<MapObject> mapObjects;
	ArrayList<Enemy> enemies;
	ArrayList<NPC> npcs;
	int matrixWidth=0, matrixHeight=0;
	int startingX=64,startingY=116;
	byte moveDirection=6;
	Color backgroundColor = Color.WHITE;
	public editorPanel(){
		setLayout(null);
		mapObjects = new ArrayList<MapObject>();
		enemies = new ArrayList<Enemy>();
		npcs = new ArrayList<NPC>();
		try{
			road = ImageIO.read(new File("./passability/road.png"));
			wall= ImageIO.read(new File("./passability/wall.png"));
			grass= ImageIO.read(new File("./passability/grass.png"));
			
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		timerDraw = new Timer(40, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					repaint();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
		});
	}
	public void paintComponent(Graphics gr){
		super.paintComponent(gr);
		gr.setColor(backgroundColor);
		for(int i=Math.max(0, scrX/klX); i<Math.min(matrixWidth, (scrX+scrW)/klX+1); i++){
			for(int j=Math.max(0, scrY/klY); j<Math.min(matrixHeight, (scrY+scrH)/klY+1); j++){
				gr.fillRect(-scrX+i*klX, -scrY+j*klY, klX, klY);
				int tileID;
				if((isMousePressed)&&(i>=Math.min(mouseX1,mouseX2))&&(i<=Math.max(mouseX1,mouseX2))&&(j>=Math.min(mouseY1,mouseY2))&&(j<=Math.max(mouseY1,mouseY2))){
					tileID = curTileID;
				}
				else{
					tileID = tileMatrix[i][j];
				}
				if(tileID!=-1){
					gr.drawImage(tileTextures[tileID], -scrX+i*klX, -scrY+j*klY, null);
				}
			}
		}
		for(int i=0;i<mapObjects.size();i++){
			MapObject obj = mapObjects.get(i);
			gr.drawImage(obj.image, obj.x-scrX, obj.y-scrY, null);
		}
		for(int i=0;i<npcs.size();i++){
			NPC obj = npcs.get(i);
			gr.drawImage(obj.image, obj.x-scrX, obj.y-scrY, null);
		}
		for(int i=0;i<enemies.size();i++){
			Enemy obj = enemies.get(i);
			gr.drawImage(obj.image, obj.x-scrX, obj.y-scrY, null);
		}
		gr.drawImage(playerTexture, startingX-scrX-64, startingY-scrY-116, null);
		if(showPassability){
			for(int i=Math.max(0, scrX/klX); i<Math.min(matrixWidth, (scrX+scrW)/klX+1); i++){
				for(int j=Math.max(0, scrY/klY); j<Math.min(matrixHeight, (scrY+scrH)/klY+1); j++){
					if((isMousePressed)&&(i>=Math.min(mouseX1,mouseX2))&&(i<=Math.max(mouseX1,mouseX2))&&(j>=Math.min(mouseY1,mouseY2))&&(j<=Math.max(mouseY1,mouseY2))){
						if(curPassability==0){
							gr.drawImage(road, -scrX+i*klX, -scrY+j*klY, null);
						}
						else if(curPassability==1){
							gr.drawImage(wall, -scrX+i*klX, -scrY+j*klY, null);
						}
						else if(curPassability==2){
							gr.drawImage(grass, -scrX+i*klX, -scrY+j*klY, null);
						}
					}
					else{
						if(matrix[i][j]==0){
							gr.drawImage(road, -scrX+i*klX, -scrY+j*klY, null);
						}
						else if(matrix[i][j]==1){
							gr.drawImage(wall, -scrX+i*klX, -scrY+j*klY, null);
						}
						else if(matrix[i][j]==2){
							gr.drawImage(grass, -scrX+i*klX, -scrY+j*klY, null);
						}
					}
				}
			}
		}
		if(selectedObjectID!=-1){
			MapObject tmp = mapObjects.get(selectedObjectID);
			gr.setColor(Color.RED);
			gr.drawRect(tmp.x-scrX, tmp.y-scrY, tmp.width, tmp.height);
		}
		else if(selectedEnemyID!=-1){
			Enemy tmp = enemies.get(selectedEnemyID);
			gr.setColor(Color.RED);
			gr.drawRect(tmp.x-scrX, tmp.y-scrY, tmp.width, tmp.height);
		}
		else if(selectedNPCID!=-1){
			NPC tmp = npcs.get(selectedNPCID);
			gr.setColor(Color.RED);
			gr.drawRect(tmp.x-scrX, tmp.y-scrY, tmp.width, tmp.height);
		}
		if(mouseX1!=-1 && !isMousePressed){
			if(curTileID!=-1){
				gr.drawImage(curTileImage, mouseX1, mouseY1, null);
			}
			else if(curObjectName!=null){
				gr.drawImage(curObjectImage, mouseX1, mouseY1, null);
			}
			else if(curEnemyID!=-1){
				gr.drawImage(curEnemyImage, mouseX1, mouseY1, null);
			}
			else if(curNPCID!=-1){
				gr.drawImage(curNPCImage, mouseX1, mouseY1, null);
			}
			
		}
		if(showGrid){
			gr.setColor(Color.BLACK);
			for(int i=0;i<=scrW/klX;i++){
				gr.drawLine(i*klX, 0, i*klX, scrH);
			}
			for(int i=0;i<=scrH/klY;i++){
				gr.drawLine(0, i*klY, scrW, i*klY);
			}
		}
	}
	public void finish(){
		
		int mapID = Integer.parseInt(JOptionPane.showInputDialog(null).trim());
		String lineSeparator = System.getProperty("line.separator");
		 try(FileWriter writer = new FileWriter("./p2edit/mapInfo.txt", false))
		 {
			 writer.write("byte[][] mapMatrix = {"+lineSeparator);
			 for(int i=0; i<matrixWidth-1; i++){
				 	writer.write("{");
					for(int j=0; j<matrixHeight-1; j++){
						writer.write(matrix[i][j]+",");
					}
					writer.write(matrix[i][matrixHeight-1]+"},"+lineSeparator);
			 }
			 	writer.write("{");
			 	for(int j=0; j<matrixHeight-1; j++){
					writer.write(matrix[matrixWidth-1][j]+",");
				}
				writer.write(matrix[matrixWidth-1][matrixHeight-1]+"}"+lineSeparator);
				writer.write("};");
	            writer.flush();
	            System.exit(0);
	        }
	        catch(IOException ex){
	            ex.printStackTrace();
	            System.exit(0);
	        } 
	}

	public void updateEnemyImage(){
		try{
			curEnemyImage = ImageIO.read(new File("./enemies/enemy"+addZeros(curEnemyID)+"6.png"));
		}
		catch(Exception ex){ex.printStackTrace();}
	}
	public void updateNPCImage(){
		try{
			curNPCImage = ImageIO.read(new File("./npcs/npc"+addZeros(curNPCID)+"6.png"));
		}
		catch(Exception ex){ex.printStackTrace();}
	}
	public void updatePlayerImage(){
		try{
			playerTexture = ImageIO.read(new File("./player/player10"+moveDirection+".png"));
		}
		catch(Exception ex){ex.printStackTrace();}
	}
	public void updateObjectImage(){
		try{
		curObjectImage = ImageIO.read(new File("./mapObjects/"+curObjectName+".png"));
		}
		catch(Exception ex){ex.printStackTrace();}
	}
	public void updateTileImage(){
		try{
		curTileImage = ImageIO.read(new File("./tiles/tile"+curTileID+".png"));
		}
		catch(Exception ex){ex.printStackTrace();}
	}
	public String addZeros(int textureID){
		String tmp="";
		if(textureID<1000){
			tmp+="0";
			if(textureID<100){
				tmp+="0";
				if(textureID<10){
					tmp+="0";
				}
			}
		}
		tmp+=textureID;
		return tmp;
	}
}
