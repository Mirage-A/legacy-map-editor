import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;

public class Enemy {
	int x,y,textureID;
	byte moveDirection;
	Image image;
	int width,height;
	public Enemy(int x, int y, int textureID, byte moveDirection){
		this.x=x;
		this.y=y;
		this.textureID=textureID;
		this.moveDirection=moveDirection;
		try{
			this.image=ImageIO.read(new File("./enemies/enemy"+addZeros()+moveDirection+".png"));
		}
		catch(Exception ex){
			System.out.println("Ќе удалось найти изображение противника "+textureID);
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
