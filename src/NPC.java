
import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;

public class NPC {
	int x,y,textureID;
	byte moveDirection;
	Image image;
	int width,height;
	public NPC(int x, int y, int textureID, byte moveDirection){
		this.x=x;
		this.y=y;
		this.textureID=textureID;
		this.moveDirection=moveDirection;
		try{
			this.image=ImageIO.read(new File("./NPCs/npc"+addZeros()+moveDirection+".png"));
		}
		catch(Exception ex){
			System.out.println("Не удалось найти изображение NPC "+textureID);
		}
		this.width=this.image.getWidth(null);
		this.height=this.image.getHeight(null);
	}
	private String addZeros(){
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
